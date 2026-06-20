package com.qianqiu.assistant.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.qianqiu.assistant.data.AppRepository
import com.qianqiu.assistant.model.AgentTask
import com.qianqiu.assistant.model.AiSettings
import com.qianqiu.assistant.model.ChatMessage
import com.qianqiu.assistant.model.CommandResult
import com.qianqiu.assistant.model.EnvironmentStatus
import com.qianqiu.assistant.model.MessageRole
import com.qianqiu.assistant.model.TaskLog
import com.qianqiu.assistant.model.TaskStatus
import com.qianqiu.assistant.model.WorkspaceFile
import com.qianqiu.assistant.runtime.AiGateway
import com.qianqiu.assistant.runtime.EnvironmentDetector
import com.qianqiu.assistant.runtime.FileWorkspaceManager
import com.qianqiu.assistant.runtime.ShellExecutor
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class QianQiuUiState(
    val messages: List<ChatMessage> = emptyList(),
    val tasks: List<AgentTask> = emptyList(),
    val logs: List<TaskLog> = emptyList(),
    val settings: AiSettings = AiSettings(),
    val environmentStatus: EnvironmentStatus = EnvironmentStatus(),
    val workspaceFiles: List<WorkspaceFile> = emptyList(),
    val workspacePath: String = "",
    val lastCommandResult: CommandResult? = null,
    val busy: Boolean = false,
    val banner: String? = null
)

class QianQiuViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = AppRepository(application)
    private val environmentDetector = EnvironmentDetector(application)
    private val shellExecutor = ShellExecutor()
    private val workspaceManager = FileWorkspaceManager(application)
    private val aiGateway = AiGateway()

    private val environmentFlow = MutableStateFlow(EnvironmentStatus())
    private val workspaceFilesFlow = MutableStateFlow(emptyList<WorkspaceFile>())
    private val workspacePathFlow = MutableStateFlow(workspaceManager.workspacePath())
    private val lastCommandFlow = MutableStateFlow<CommandResult?>(null)
    private val busyFlow = MutableStateFlow(false)
    private val bannerFlow = MutableStateFlow<String?>(null)

    val uiState: StateFlow<QianQiuUiState> = combine(
        repository.messages,
        repository.tasks,
        repository.logs,
        repository.settings,
        environmentFlow,
        workspaceFilesFlow,
        workspacePathFlow,
        lastCommandFlow,
        busyFlow,
        bannerFlow
    ) { values ->
        QianQiuUiState(
            messages = values[0] as List<ChatMessage>,
            tasks = values[1] as List<AgentTask>,
            logs = values[2] as List<TaskLog>,
            settings = values[3] as AiSettings,
            environmentStatus = values[4] as EnvironmentStatus,
            workspaceFiles = values[5] as List<WorkspaceFile>,
            workspacePath = values[6] as String,
            lastCommandResult = values[7] as CommandResult?,
            busy = values[8] as Boolean,
            banner = values[9] as String?
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), QianQiuUiState())

    init {
        viewModelScope.launch {
            repository.bootstrap()
            refreshEnvironment()
            refreshWorkspaceFiles()
        }
    }

    fun saveAiSettings(baseUrl: String, apiKey: String, model: String) {
        viewModelScope.launch {
            repository.saveAiSettings(
                AiSettings(
                    baseUrl = baseUrl,
                    apiKey = apiKey,
                    model = model
                )
            )
            bannerFlow.value = "AI 接口配置已保存"
        }
    }

    fun refreshEnvironment() {
        viewModelScope.launch {
            environmentFlow.value = environmentDetector.detect()
        }
    }

    fun sendMessage(prompt: String) {
        val text = prompt.trim()
        if (text.isEmpty()) return

        viewModelScope.launch {
            busyFlow.value = true
            bannerFlow.value = null
            try {
                repository.insertMessage(MessageRole.USER, text)

                val taskId = repository.createTask(
                    title = "AI 对话请求",
                    detail = text,
                    status = TaskStatus.RUNNING,
                    progress = 15,
                    currentStep = "正在整理上下文"
                )
                repository.addTaskLog(taskId, "INFO", "收到用户目标: $text")

                val settings = uiState.value.settings
                val messages = repository.messages.value
                val env = environmentDetector.detect()
                environmentFlow.value = env

                repository.updateTask(
                    id = taskId,
                    status = TaskStatus.RUNNING,
                    progress = 55,
                    currentStep = "正在生成回复",
                    retryCount = 0,
                    detail = if (settings.apiKey.isBlank()) "使用离线回退回复" else "调用 ${settings.model}"
                )

                val result = aiGateway.generateReply(settings, messages)
                val reply = if (result.isSuccess) {
                    result.getOrThrow()
                } else {
                    repository.addTaskLog(
                        taskId,
                        "WARN",
                        "AI 接口不可用，切换离线回退: ${result.exceptionOrNull()?.message}"
                    )
                    aiGateway.fallbackReply(text, summarizeEnvironment(env))
                }

                repository.insertMessage(MessageRole.ASSISTANT, reply)
                repository.updateTask(
                    id = taskId,
                    status = TaskStatus.SUCCESS,
                    progress = 100,
                    currentStep = "对话已完成",
                    retryCount = 0,
                    detail = reply.take(120)
                )
                repository.addTaskLog(taskId, "INFO", "对话完成")
            } finally {
                busyFlow.value = false
            }
        }
    }

    fun runCommand(command: String) {
        val normalized = command.trim()
        if (normalized.isEmpty()) return

        viewModelScope.launch {
            if (isDangerousCommand(normalized)) {
                val taskId = repository.createTask(
                    title = "危险命令已拦截",
                    detail = normalized,
                    status = TaskStatus.FAILED,
                    progress = 100,
                    currentStep = "已阻止执行"
                )
                repository.addTaskLog(taskId, "WARN", "命中基础安全规则，已阻止执行危险命令")
                bannerFlow.value = "已拦截高风险命令，请改用更安全的操作方式"
                return@launch
            }

            busyFlow.value = true
            bannerFlow.value = null
            try {
                val taskId = repository.createTask(
                    title = "执行命令",
                    detail = normalized,
                    status = TaskStatus.RUNNING,
                    progress = 20,
                    currentStep = "准备执行 Shell"
                )
                repository.addTaskLog(taskId, "INFO", "执行命令: $normalized")

                val result = shellExecutor.run(normalized, maxAttempts = 2)
                lastCommandFlow.value = result

                val detail = buildString {
                    append("退出码=${result.exitCode}，耗时=${result.durationMs}ms")
                    if (result.stderr.isNotBlank()) append("，stderr=${result.stderr.take(100)}")
                }

                repository.addTaskLog(taskId, if (result.exitCode == 0) "INFO" else "ERROR", detail)
                repository.addTaskLog(taskId, "INFO", "stdout: ${result.stdout.ifBlank { "<empty>" }.take(300)}")

                repository.updateTask(
                    id = taskId,
                    status = if (result.exitCode == 0) TaskStatus.SUCCESS else TaskStatus.FAILED,
                    progress = 100,
                    currentStep = if (result.exitCode == 0) "命令执行成功" else "命令执行失败",
                    retryCount = (result.attempts - 1).coerceAtLeast(0),
                    detail = detail
                )
            } finally {
                busyFlow.value = false
            }
        }
    }

    fun createOrUpdateFile(name: String, content: String) {
        val safeName = name.trim()
        if (safeName.isEmpty()) return

        viewModelScope.launch {
            val taskId = repository.createTask(
                title = "写入文件",
                detail = safeName,
                status = TaskStatus.RUNNING,
                progress = 35,
                currentStep = "正在写入工作区文件"
            )
            try {
                val saved = workspaceManager.createOrUpdateFile(safeName, content)
                refreshWorkspaceFiles()
                repository.addTaskLog(taskId, "INFO", "已写入 ${saved.name}")
                repository.updateTask(
                    id = taskId,
                    status = TaskStatus.SUCCESS,
                    progress = 100,
                    currentStep = "文件已保存",
                    retryCount = 0,
                    detail = "${saved.name} 已更新"
                )
                bannerFlow.value = "文件已保存到工作区"
            } catch (error: Exception) {
                repository.addTaskLog(taskId, "ERROR", "文件写入失败: ${error.message}")
                repository.updateTask(
                    id = taskId,
                    status = TaskStatus.FAILED,
                    progress = 100,
                    currentStep = "文件保存失败",
                    retryCount = 0,
                    detail = error.message ?: "未知错误"
                )
                bannerFlow.value = "文件写入失败"
            }
        }
    }

    fun deleteFile(name: String) {
        val safeName = name.trim()
        if (safeName.isEmpty()) return

        viewModelScope.launch {
            val taskId = repository.createTask(
                title = "删除文件",
                detail = safeName,
                status = TaskStatus.RUNNING,
                progress = 45,
                currentStep = "正在删除工作区文件"
            )
            try {
                val deleted = workspaceManager.deleteFile(safeName)
                refreshWorkspaceFiles()
                repository.addTaskLog(taskId, if (deleted) "INFO" else "WARN", if (deleted) "已删除 $safeName" else "文件不存在: $safeName")
                repository.updateTask(
                    id = taskId,
                    status = if (deleted) TaskStatus.SUCCESS else TaskStatus.FAILED,
                    progress = 100,
                    currentStep = if (deleted) "文件已删除" else "删除失败",
                    retryCount = 0,
                    detail = if (deleted) "$safeName 已删除" else "$safeName 不存在"
                )
                bannerFlow.value = if (deleted) "文件已删除" else "未找到要删除的文件"
            } catch (error: Exception) {
                repository.addTaskLog(taskId, "ERROR", "文件删除失败: ${error.message}")
                repository.updateTask(
                    id = taskId,
                    status = TaskStatus.FAILED,
                    progress = 100,
                    currentStep = "删除失败",
                    retryCount = 0,
                    detail = error.message ?: "未知错误"
                )
                bannerFlow.value = "文件删除失败"
            }
        }
    }

    fun clearBanner() {
        bannerFlow.value = null
    }

    private fun refreshWorkspaceFiles() {
        workspaceFilesFlow.value = workspaceManager.listFiles()
        workspacePathFlow.value = workspaceManager.workspacePath()
    }

    private fun summarizeEnvironment(status: EnvironmentStatus): String {
        return "Termux=${status.termuxInstalled}，Termux API=${status.termuxApiInstalled}，Shizuku=${status.shizukuInstalled}，当前推荐通道=${status.preferredChannel}"
    }

    private fun isDangerousCommand(command: String): Boolean {
        val lowered = command.lowercase()
        val blockedPatterns = listOf(
            "rm -rf /",
            "rm -rf /*",
            "reboot",
            "poweroff",
            "shutdown",
            "mkfs",
            "dd if=",
            ":(){:|:&};:"
        )
        return blockedPatterns.any { it in lowered }
    }
}

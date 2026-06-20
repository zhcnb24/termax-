package com.qianqiu.assistant.data

import android.content.Context
import com.qianqiu.assistant.model.AgentTask
import com.qianqiu.assistant.model.AiSettings
import com.qianqiu.assistant.model.ChatMessage
import com.qianqiu.assistant.model.MessageRole
import com.qianqiu.assistant.model.TaskLog
import com.qianqiu.assistant.model.TaskStatus
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext

class AppRepository(context: Context) {
    private val db = AppDatabaseHelper(context)

    private val _messages = MutableStateFlow(emptyList<ChatMessage>())
    val messages: StateFlow<List<ChatMessage>> = _messages.asStateFlow()

    private val _tasks = MutableStateFlow(emptyList<AgentTask>())
    val tasks: StateFlow<List<AgentTask>> = _tasks.asStateFlow()

    private val _logs = MutableStateFlow(emptyList<TaskLog>())
    val logs: StateFlow<List<TaskLog>> = _logs.asStateFlow()

    private val _settings = MutableStateFlow(AiSettings())
    val settings: StateFlow<AiSettings> = _settings.asStateFlow()

    suspend fun bootstrap() = withContext(Dispatchers.IO) {
        refreshAll()
        if (_messages.value.isEmpty()) {
            insertMessage(
                role = MessageRole.SYSTEM,
                content = "千秋已切换到第二阶段最小可用版，支持环境检测、任务记录、Shell 执行、文件操作和可配置 AI 对话。"
            )
        }
    }

    suspend fun insertMessage(role: MessageRole, content: String): Long = withContext(Dispatchers.IO) {
        val id = db.insertMessage(role, content.trim(), System.currentTimeMillis())
        _messages.value = db.getMessages()
        id
    }

    suspend fun createTask(
        title: String,
        detail: String,
        status: TaskStatus = TaskStatus.QUEUED,
        progress: Int = 0,
        currentStep: String = "等待执行",
        retryCount: Int = 0
    ): Long = withContext(Dispatchers.IO) {
        val now = System.currentTimeMillis()
        val id = db.insertTask(
            title = title,
            detail = detail,
            status = status,
            progress = progress,
            currentStep = currentStep,
            retryCount = retryCount,
            createdAt = now,
            updatedAt = now
        )
        _tasks.value = db.getTasks()
        id
    }

    suspend fun updateTask(
        id: Long,
        status: TaskStatus,
        progress: Int,
        currentStep: String,
        retryCount: Int,
        detail: String
    ) = withContext(Dispatchers.IO) {
        db.updateTask(
            id = id,
            status = status,
            progress = progress,
            currentStep = currentStep,
            retryCount = retryCount,
            detail = detail,
            updatedAt = System.currentTimeMillis()
        )
        _tasks.value = db.getTasks()
    }

    suspend fun addTaskLog(taskId: Long, level: String, message: String) = withContext(Dispatchers.IO) {
        db.insertTaskLog(taskId, level, message.trim(), System.currentTimeMillis())
        _logs.value = db.getTaskLogs()
    }

    suspend fun saveAiSettings(settings: AiSettings) = withContext(Dispatchers.IO) {
        db.saveSetting("ai_base_url", settings.baseUrl.trim())
        db.saveSetting("ai_api_key", settings.apiKey.trim())
        db.saveSetting("ai_model", settings.model.trim())
        _settings.value = db.loadAiSettings()
    }

    suspend fun refreshAll() = withContext(Dispatchers.IO) {
        _messages.value = db.getMessages()
        _tasks.value = db.getTasks()
        _logs.value = db.getTaskLogs()
        _settings.value = db.loadAiSettings()
    }
}

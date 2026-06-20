package com.qianqiu.assistant.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.weight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material.icons.filled.TaskAlt
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.qianqiu.assistant.model.AgentTask
import com.qianqiu.assistant.model.ChatMessage
import com.qianqiu.assistant.model.MessageRole
import com.qianqiu.assistant.model.TaskLog
import com.qianqiu.assistant.model.TaskStatus
import com.qianqiu.assistant.model.WorkspaceFile

private enum class MainTab(
    val title: String,
    val icon: ImageVector
) {
    Home("首页", Icons.Default.Home),
    Chat("AI聊天", Icons.Default.SmartToy),
    Workbench("工作台", Icons.Default.Terminal),
    Tasks("任务中心", Icons.Default.TaskAlt),
    Profile("我的", Icons.Default.Person)
}

@Composable
fun QianQiuApp(viewModel: QianQiuViewModel = viewModel()) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    var selectedTab by rememberSaveable { mutableStateOf(MainTab.Home) }

    val backgroundBrush = Brush.linearGradient(
        colors = listOf(
            Color(0xFF0A0D18),
            Color(0xFF161B30),
            Color(0xFF0F1324)
        )
    )

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = Color.Transparent,
        bottomBar = {
            NavigationBar(
                modifier = Modifier.navigationBarsPadding(),
                containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.96f)
            ) {
                MainTab.entries.forEach { tab ->
                    NavigationBarItem(
                        selected = selectedTab == tab,
                        onClick = { selectedTab = tab },
                        icon = { Icon(tab.icon, contentDescription = tab.title) },
                        label = { Text(text = tab.title) }
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(backgroundBrush)
                .padding(innerPadding)
        ) {
            when (selectedTab) {
                MainTab.Home -> HomeScreen(state = state)
                MainTab.Chat -> ChatScreen(state = state, viewModel = viewModel)
                MainTab.Workbench -> WorkbenchScreen(state = state, viewModel = viewModel)
                MainTab.Tasks -> TasksScreen(state = state)
                MainTab.Profile -> ProfileScreen(state = state)
            }
        }
    }
}

@Composable
private fun HomeScreen(state: QianQiuUiState) {
    val successCount = state.tasks.count { it.status == TaskStatus.SUCCESS }
    val failedCount = state.tasks.count { it.status == TaskStatus.FAILED }

    ScreenContainer {
        HeroCard(
            title = "Termux 千秋辅助",
            subtitle = "第二阶段最小可用版",
            body = "当前版本不再只是展示原型，已经具备聊天接口接入、任务状态流转、环境检测、Shell 执行、文件操作和本地持久化能力。"
        )
        state.banner?.let {
            BannerCard(message = it)
        }
        GlassCard(title = "实时总览", icon = Icons.Default.AutoAwesome) {
            StatLine(label = "消息数", value = "${state.messages.size}")
            StatLine(label = "任务成功", value = "$successCount")
            StatLine(label = "任务失败", value = "$failedCount")
            StatLine(label = "工作区文件", value = "${state.workspaceFiles.size}")
            StatLine(label = "推荐执行通道", value = state.environmentStatus.preferredChannel)
        }
        GlassCard(title = "阶段能力", icon = Icons.Default.Build) {
            TagRow(
                tags = listOf(
                    "AI 对话接口",
                    "离线回退回复",
                    "Termux 检测",
                    "命令执行",
                    "文件操作",
                    "本地 SQLite",
                    "任务日志",
                    "失败重试"
                )
            )
        }
        GlassCard(title = "环境状态", icon = Icons.Default.Security) {
            EnvironmentRow("Termux", state.environmentStatus.termuxInstalled)
            EnvironmentRow("Termux API", state.environmentStatus.termuxApiInstalled)
            EnvironmentRow("Shizuku", state.environmentStatus.shizukuInstalled)
        }
        GlassCard(title = "产品信条", icon = Icons.Default.Memory) {
            Text(
                text = "纵使前方万般难，踏步前行入平川。",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
private fun ChatScreen(state: QianQiuUiState, viewModel: QianQiuViewModel) {
    var chatInput by rememberSaveable { mutableStateOf("") }
    var baseUrl by rememberSaveable { mutableStateOf(state.settings.baseUrl) }
    var apiKey by rememberSaveable { mutableStateOf(state.settings.apiKey) }
    var model by rememberSaveable { mutableStateOf(state.settings.model) }

    LaunchedEffect(state.settings.baseUrl, state.settings.apiKey, state.settings.model) {
        baseUrl = state.settings.baseUrl
        apiKey = state.settings.apiKey
        model = state.settings.model
    }

    ScreenContainer {
        HeroCard(
            title = "AI 角色: 千秋",
            subtitle = "工作模式已接管",
            body = "支持 OpenAI 兼容接口接入。未配置密钥时，将自动启用离线回退回复，不会让聊天链路完全空转。"
        )
        GlassCard(title = "AI 接口配置", icon = Icons.Default.Key) {
            OutlinedTextField(
                value = baseUrl,
                onValueChange = { baseUrl = it },
                label = { Text("Base URL") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            OutlinedTextField(
                value = model,
                onValueChange = { model = it },
                label = { Text("模型名称") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            OutlinedTextField(
                value = apiKey,
                onValueChange = { apiKey = it },
                label = { Text("API Key") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                visualTransformation = PasswordVisualTransformation()
            )
            Button(
                onClick = { viewModel.saveAiSettings(baseUrl, apiKey, model) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Default.Save, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("保存对话配置")
            }
        }
        GlassCard(title = "对话区", icon = Icons.Default.SmartToy) {
            if (state.messages.isEmpty()) {
                Text("暂无消息", color = MaterialTheme.colorScheme.onSurfaceVariant)
            } else {
                state.messages.forEach { message ->
                    MessageBubble(message = message)
                }
            }
            OutlinedTextField(
                value = chatInput,
                onValueChange = { chatInput = it },
                label = { Text("输入目标，例如：帮我检查当前执行环境") },
                modifier = Modifier.fillMaxWidth()
            )
            Button(
                onClick = {
                    viewModel.sendMessage(chatInput)
                    chatInput = ""
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = !state.busy
            ) {
                if (state.busy) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(18.dp),
                        strokeWidth = 2.dp
                    )
                } else {
                    Icon(Icons.Default.Send, contentDescription = null)
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(if (state.busy) "千秋处理中..." else "发送")
            }
        }
    }
}

@Composable
private fun WorkbenchScreen(state: QianQiuUiState, viewModel: QianQiuViewModel) {
    var command by rememberSaveable { mutableStateOf("pwd") }
    var fileName by rememberSaveable { mutableStateOf("demo.txt") }
    var fileContent by rememberSaveable { mutableStateOf("Termux 千秋辅助工作区文件") }

    ScreenContainer {
        HeroCard(
            title = "代码工作台",
            subtitle = "真实执行与文件落地",
            body = "这里已经接入命令执行、错误回显、自动重试和应用私有工作区文件管理，是第二阶段 MVP 的核心执行面板。"
        )
        GlassCard(title = "环境检测", icon = Icons.Default.Refresh) {
            Text("当前推荐通道：${state.environmentStatus.preferredChannel}")
            EnvironmentRow("Termux", state.environmentStatus.termuxInstalled)
            EnvironmentRow("Termux API", state.environmentStatus.termuxApiInstalled)
            EnvironmentRow("Shizuku", state.environmentStatus.shizukuInstalled)
            Button(
                onClick = { viewModel.refreshEnvironment() },
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Default.Refresh, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("重新检测环境")
            }
        }
        GlassCard(title = "命令执行", icon = Icons.Default.Terminal) {
            OutlinedTextField(
                value = command,
                onValueChange = { command = it },
                label = { Text("Shell 命令") },
                modifier = Modifier.fillMaxWidth()
            )
            Button(
                onClick = { viewModel.runCommand(command) },
                modifier = Modifier.fillMaxWidth(),
                enabled = !state.busy
            ) {
                Icon(Icons.Default.PlayArrow, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("执行命令")
            }
            state.lastCommandResult?.let { result ->
                Divider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
                StatLine(label = "退出码", value = "${result.exitCode}")
                StatLine(label = "耗时", value = "${result.durationMs}ms")
                StatLine(label = "重试次数", value = "${(result.attempts - 1).coerceAtLeast(0)}")
                ResultBlock(title = "stdout", content = result.stdout.ifBlank { "<empty>" })
                ResultBlock(title = "stderr", content = result.stderr.ifBlank { "<empty>" })
            }
        }
        GlassCard(title = "工作区文件", icon = Icons.Default.Folder) {
            StatLine(label = "目录", value = state.workspacePath)
            OutlinedTextField(
                value = fileName,
                onValueChange = { fileName = it },
                label = { Text("文件名") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            OutlinedTextField(
                value = fileContent,
                onValueChange = { fileContent = it },
                label = { Text("文件内容") },
                modifier = Modifier.fillMaxWidth()
            )
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(
                    onClick = { viewModel.createOrUpdateFile(fileName, fileContent) },
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.Save, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("保存")
                }
                Button(
                    onClick = { viewModel.deleteFile(fileName) },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Icon(Icons.Default.Delete, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("删除")
                }
            }
            if (state.workspaceFiles.isEmpty()) {
                Text("工作区还没有文件", color = MaterialTheme.colorScheme.onSurfaceVariant)
            } else {
                state.workspaceFiles.forEach { file ->
                    WorkspaceFileCard(
                        file = file,
                        onLoad = {
                            fileName = file.name
                            fileContent = file.content
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun TasksScreen(state: QianQiuUiState) {
    ScreenContainer {
        HeroCard(
            title = "任务中心",
            subtitle = "真实状态流转",
            body = "这里展示当前版本实际产生的任务、执行状态、进度、日志和失败记录，不再是静态展示文案。"
        )
        if (state.tasks.isEmpty()) {
            GlassCard(title = "暂无任务", icon = Icons.Default.Info) {
                Text("进入聊天、工作台执行操作后，这里会持续累积任务记录。")
            }
        } else {
            state.tasks.forEach { task ->
                TaskCard(task = task)
            }
        }
        GlassCard(title = "最近日志", icon = Icons.Default.Storage) {
            if (state.logs.isEmpty()) {
                Text("暂无日志", color = MaterialTheme.colorScheme.onSurfaceVariant)
            } else {
                state.logs.take(20).forEach { log ->
                    LogRow(log = log)
                }
            }
        }
    }
}

@Composable
private fun ProfileScreen(state: QianQiuUiState) {
    ScreenContainer {
        HeroCard(
            title = "个人中心",
            subtitle = "当前交付状态",
            body = "这一版先聚焦最小可用，不做会员墙、不做花哨空壳，优先保证你能在 Android 端看见真实链路开始工作。"
        )
        GlassCard(title = "本地持久化", icon = Icons.Default.Storage) {
            TagRow(tags = listOf("SQLite 消息表", "任务表", "日志表", "AI 配置表"))
        }
        GlassCard(title = "下一阶段", icon = Icons.Default.Settings) {
            TagRow(tags = listOf("Agent 拆任务", "执行校验", "自动修复", "安全确认", "模式切换"))
        }
        GlassCard(title = "当前统计", icon = Icons.Default.Person) {
            StatLine(label = "消息存档", value = "${state.messages.size}")
            StatLine(label = "任务存档", value = "${state.tasks.size}")
            StatLine(label = "日志存档", value = "${state.logs.size}")
        }
    }
}

@Composable
private fun ScreenContainer(content: @Composable Column.() -> Unit) {
    val topPadding = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(start = 16.dp, end = 16.dp, top = topPadding + 16.dp, bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        content = content
    )
}

@Composable
private fun HeroCard(title: String, subtitle: String, body: String) {
    Card(
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.82f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.linearGradient(
                        listOf(Color(0xAA7C8CFF), Color(0x5530D0FF), Color(0x229C6FFF))
                    )
                )
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Surface(shape = CircleShape, color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.16f)) {
                Text(
                    text = "千秋",
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    color = Color.White,
                    style = MaterialTheme.typography.labelLarge
                )
            }
            Text(
                text = title,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.titleMedium,
                color = Color(0xFFF2F5FF)
            )
            Text(
                text = body,
                style = MaterialTheme.typography.bodyLarge,
                color = Color(0xFFE7ECFF)
            )
        }
    }
}

@Composable
private fun BannerCard(message: String) {
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.16f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Icon(Icons.Default.Info, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
            Text(message, color = MaterialTheme.colorScheme.onSurface)
        }
    }
}

@Composable
private fun GlassCard(title: String, icon: ImageVector, body: @Composable Column.() -> Unit) {
    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.72f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.16f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(icon, contentDescription = title, tint = MaterialTheme.colorScheme.primary)
                }
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.SemiBold
                )
            }
            body()
        }
    }
}

@Composable
private fun MessageBubble(message: ChatMessage) {
    val isUser = message.role == MessageRole.USER
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start
    ) {
        Card(
            shape = RoundedCornerShape(22.dp),
            colors = CardDefaults.cardColors(
                containerColor = if (isUser) {
                    MaterialTheme.colorScheme.primary.copy(alpha = 0.22f)
                } else {
                    MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.84f)
                }
            ),
            modifier = Modifier.fillMaxWidth(0.9f)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = when (message.role) {
                        MessageRole.USER -> "你"
                        MessageRole.ASSISTANT -> "千秋"
                        MessageRole.SYSTEM -> "系统"
                    },
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = message.content,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}

@Composable
private fun TaskCard(task: AgentTask) {
    val color = when (task.status) {
        TaskStatus.SUCCESS -> Color(0xFF64F0A7)
        TaskStatus.FAILED -> MaterialTheme.colorScheme.error
        TaskStatus.RUNNING -> Color(0xFF85D6FF)
        TaskStatus.QUEUED -> Color(0xFFFFD66B)
    }

    Card(
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.76f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(12.dp)
                        .clip(CircleShape)
                        .background(color)
                )
                Text(
                    text = task.title,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.SemiBold
                )
            }
            StatLine(label = "状态", value = task.status.name)
            StatLine(label = "进度", value = "${task.progress}%")
            StatLine(label = "步骤", value = task.currentStep)
            StatLine(label = "重试", value = "${task.retryCount}")
            Text(
                text = task.detail,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Composable
private fun LogRow(log: TaskLog) {
    val icon = when (log.level.uppercase()) {
        "ERROR" -> Icons.Default.Error
        "WARN" -> Icons.Default.Warning
        else -> Icons.Default.Info
    }
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.Top
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(18.dp)
        )
        Text(
            text = log.message,
            color = MaterialTheme.colorScheme.onSurface,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@Composable
private fun WorkspaceFileCard(file: WorkspaceFile, onLoad: () -> Unit) {
    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.65f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = file.name,
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Button(onClick = onLoad) {
                    Icon(Icons.Default.Code, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("载入")
                }
            }
            Text(
                text = file.content.ifBlank { "<empty>" },
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 5,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun ResultBlock(title: String, content: String) {
    Text(text = title, color = MaterialTheme.colorScheme.primary, style = MaterialTheme.typography.labelLarge)
    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.68f)
        )
    ) {
        Text(
            text = content,
            modifier = Modifier.padding(14.dp),
            color = MaterialTheme.colorScheme.onSurface,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@Composable
private fun StatLine(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = value,
            color = MaterialTheme.colorScheme.onSurface,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@Composable
private fun EnvironmentRow(label: String, installed: Boolean) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, color = MaterialTheme.colorScheme.onSurface)
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = if (installed) Icons.Default.CheckCircle else Icons.Default.Warning,
                contentDescription = null,
                tint = if (installed) Color(0xFF64F0A7) else Color(0xFFFFD66B),
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(if (installed) "已安装" else "未安装")
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun TagRow(tags: List<String>) {
    FlowRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        tags.forEach { tag ->
            AssistChip(onClick = {}, label = { Text(text = tag) })
        }
    }
}

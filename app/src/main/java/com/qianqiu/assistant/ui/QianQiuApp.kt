package com.qianqiu.assistant.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Extension
import androidx.compose.material.icons.filled.FolderZip
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Hub
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material.icons.filled.TaskAlt
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

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

private data class FeatureCard(
    val title: String,
    val desc: String,
    val icon: ImageVector
)

@Composable
fun QianQiuApp() {
    var selectedTab by remember { mutableStateOf(MainTab.Home) }

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
                MainTab.Home -> HomeScreen()
                MainTab.Chat -> ChatScreen()
                MainTab.Workbench -> WorkbenchScreen()
                MainTab.Tasks -> TasksScreen()
                MainTab.Profile -> ProfileScreen()
            }
        }
    }
}

@Composable
private fun HomeScreen() {
    val features = listOf(
        FeatureCard("Termux 执行核心", "Shizuku / Termux API / Intent 自动择优", Icons.Default.Terminal),
        FeatureCard("超级 Agent", "分析目标、拆解任务、执行与自修复闭环", Icons.Default.AutoAwesome),
        FeatureCard("三级长期记忆", "重要资料、项目经验、日常记录分层沉淀", Icons.Default.Memory),
        FeatureCard("插件市场", "Python、Shell、NodeJS、Java 插件按权限分级", Icons.Default.Extension),
        FeatureCard("沙盒与快照", "先测试后落地，支持回滚和断点续传", Icons.Default.Security),
        FeatureCard("知识库导入", "支持 PDF、TXT、MD、ZIP 学习与归档", Icons.Default.FolderZip)
    )

    ScreenContainer {
        HeroCard(
            title = "Termux 千秋辅助",
            subtitle = "Android 超级 AI Agent 开发平台",
            body = "融合 Termux、AI Agent、Cursor、Claude Code 与 Manus 的移动端智能开发体验。"
        )
        GlassCard(title = "设计语言", icon = Icons.Default.Build) {
            TagRow(
                tags = listOf(
                    "70% Telegram",
                    "20% Cursor",
                    "10% VSCode",
                    "深色优先",
                    "磨砂玻璃",
                    "二次元融合",
                    "圆角动效"
                )
            )
        }
        Text(
            text = "核心能力",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(top = 4.dp, bottom = 8.dp)
        )
        features.forEach { item ->
            GlassListCard(title = item.title, desc = item.desc, icon = item.icon)
        }
        GlassCard(title = "产品信条", icon = Icons.Default.Hub) {
            Text(
                text = "纵使前方万般难，踏步前行入平川。",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
private fun ChatScreen() {
    val messages = listOf(
        "千秋已进入工作模式，准备分析你的目标。",
        "建议优先检测 Termux 与 Shizuku 状态，再构建执行沙盒。",
        "当前产品原型已展示聊天、工作台、任务中心与记忆体系。"
    )

    ScreenContainer {
        HeroCard(
            title = "AI 角色: 千秋",
            subtitle = "温柔陪伴 / 专业工程师 / Agent 执行官",
            body = "支持自定义人格、立绘、声音、角色卡导入与 JSON / PNG 角色配置。"
        )
        GlassCard(title = "对话能力", icon = Icons.Default.SmartToy) {
            TagRow(
                tags = listOf(
                    "联网搜索",
                    "代码生成",
                    "自动执行",
                    "错误修复",
                    "继续执行",
                    "危险确认"
                )
            )
        }
        messages.forEachIndexed { index, message ->
            MessageBubble(
                speaker = if (index == 0) "千秋" else "系统",
                content = message
            )
        }
    }
}

@Composable
private fun WorkbenchScreen() {
    val modules = listOf(
        "代码卡片输出与一键执行",
        "Termux 命令回显与错误诊断",
        "项目结构自动生成与文件编辑",
        "本地 / 云端 / 混合模型调度",
        "GGUF 模型识别与自动加载"
    )

    ScreenContainer {
        HeroCard(
            title = "代码工作台",
            subtitle = "手机端优先的工程执行面板",
            body = "围绕聊天驱动开发，把生成代码、执行状态、错误定位和自动修复集中在同一工作流。"
        )
        GlassCard(title = "工作模式", icon = Icons.Default.Terminal) {
            TagRow(tags = listOf("普通模式", "专家模式", "开发者模式", "任务日志", "持续通知"))
        }
        GlassCard(title = "首版模块", icon = Icons.Default.Build) {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                modules.forEach { item ->
                    Text(
                        text = "• $item",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
    }
}

@Composable
private fun TasksScreen() {
    val taskSteps = listOf(
        "正在帮你检测 Android / Termux 环境",
        "正在创建项目目录与 Gradle 架构",
        "正在生成首页、聊天页、工作台与任务中心原型",
        "正在准备 APK 构建产物"
    )

    ScreenContainer {
        HeroCard(
            title = "任务中心",
            subtitle = "口语化执行提示 + 实时进度",
            body = "持续通知当前任务、步骤、错误信息，并支持中断恢复、断点续传和后台恢复。"
        )
        taskSteps.forEach { step ->
            StepCard(step = step)
        }
        GlassCard(title = "安全规则", icon = Icons.Default.Security) {
            TagRow(
                tags = listOf(
                    "系统文件保护",
                    "重要数据保护",
                    "高风险操作确认",
                    "用户自定义规则",
                    "快照回滚"
                )
            )
        }
    }
}

@Composable
private fun ProfileScreen() {
    ScreenContainer {
        HeroCard(
            title = "个人中心",
            subtitle = "永久免费，无会员墙",
            body = "支持手机号、邮箱、第三方登录，并预留云同步、用户数据同步与多语言扩展能力。"
        )
        GlassCard(title = "登录与同步", icon = Icons.Default.Person) {
            TagRow(tags = listOf("手机号", "邮箱", "第三方登录", "云同步接口", "数据同步接口"))
        }
        GlassCard(title = "知识体系", icon = Icons.Default.Memory) {
            TagRow(tags = listOf("用户知识库", "技术知识库", "项目知识库", "任务知识库"))
        }
        GlassCard(title = "插件权限", icon = Icons.Default.Extension) {
            TagRow(tags = listOf("读取文件", "修改文件", "执行命令", "Shizuku", "Root"))
        }
    }
}

@Composable
private fun ScreenContainer(
    content: @Composable ColumnScope.() -> Unit
) {
    val topPadding = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = topPadding + 16.dp, bottom = 24.dp)
    ) {
        item {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                content()
            }
        }
    }
}

@Composable
private fun HeroCard(
    title: String,
    subtitle: String,
    body: String
) {
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
            Surface(
                shape = CircleShape,
                color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.16f)
            ) {
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
private fun GlassCard(
    title: String,
    icon: ImageVector,
    body: @Composable () -> Unit
) {
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
                    Icon(
                        imageVector = icon,
                        contentDescription = title,
                        tint = MaterialTheme.colorScheme.primary
                    )
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
private fun GlassListCard(
    title: String,
    desc: String,
    icon: ImageVector
) {
    GlassCard(title = title, icon = icon) {
        Text(
            text = desc,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun MessageBubble(
    speaker: String,
    content: String
) {
    Card(
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.84f)
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = speaker,
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = content,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
private fun StepCard(step: String) {
    Card(
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.76f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(12.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF64F0A7))
            )
            Text(
                text = step,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
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
            AssistChip(
                onClick = { },
                label = { Text(text = tag) }
            )
        }
    }
}

package com.qianqiu.assistant.model

enum class MessageRole {
    USER,
    ASSISTANT,
    SYSTEM
}

data class ChatMessage(
    val id: Long,
    val role: MessageRole,
    val content: String,
    val createdAt: Long
)

enum class TaskStatus {
    QUEUED,
    RUNNING,
    SUCCESS,
    FAILED
}

data class AgentTask(
    val id: Long,
    val title: String,
    val detail: String,
    val status: TaskStatus,
    val progress: Int,
    val currentStep: String,
    val retryCount: Int,
    val createdAt: Long,
    val updatedAt: Long
)

data class TaskLog(
    val id: Long,
    val taskId: Long,
    val level: String,
    val message: String,
    val createdAt: Long
)

data class EnvironmentStatus(
    val termuxInstalled: Boolean = false,
    val termuxApiInstalled: Boolean = false,
    val shizukuInstalled: Boolean = false,
    val preferredChannel: String = "Intent",
    val lastCheckedAt: Long = 0L
)

data class AiSettings(
    val baseUrl: String = "https://api.openai.com/v1/chat/completions",
    val apiKey: String = "",
    val model: String = "gpt-4o-mini"
)

data class CommandResult(
    val command: String,
    val exitCode: Int,
    val stdout: String,
    val stderr: String,
    val durationMs: Long,
    val attempts: Int
)

data class WorkspaceFile(
    val name: String,
    val content: String,
    val updatedAt: Long
)

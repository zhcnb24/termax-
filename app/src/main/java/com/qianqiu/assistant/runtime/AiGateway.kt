package com.qianqiu.assistant.runtime

import com.qianqiu.assistant.model.AiSettings
import com.qianqiu.assistant.model.ChatMessage
import com.qianqiu.assistant.model.MessageRole
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

class AiGateway {
    private val client = OkHttpClient.Builder()
        .connectTimeout(20, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    fun generateReply(settings: AiSettings, messages: List<ChatMessage>): Result<String> {
        if (settings.apiKey.isBlank()) {
            return Result.failure(IllegalStateException("未配置 API Key"))
        }

        return runCatching {
            val payload = JSONObject().apply {
                put("model", settings.model)
                put("temperature", 0.4)
                put(
                    "messages",
                    JSONArray().apply {
                        put(
                            JSONObject().apply {
                                put("role", "system")
                                put(
                                    "content",
                                    "你是 Android 超级 AI Agent 助手“千秋”。回复使用中文，简洁、专业、可执行，优先给出下一步行动建议。"
                                )
                            }
                        )
                        messages.takeLast(12).forEach { message ->
                            put(
                                JSONObject().apply {
                                    put("role", message.role.toWireRole())
                                    put("content", message.content)
                                }
                            )
                        }
                    }
                )
            }

            val request = Request.Builder()
                .url(settings.baseUrl)
                .header("Authorization", "Bearer ${settings.apiKey}")
                .header("Content-Type", "application/json")
                .post(payload.toString().toRequestBody("application/json; charset=utf-8".toMediaType()))
                .build()

            client.newCall(request).execute().use { response ->
                val body = response.body?.string().orEmpty()
                if (!response.isSuccessful) {
                    error("AI 请求失败: HTTP ${response.code} ${response.message} ${body.take(300)}")
                }
                val json = JSONObject(body)
                json.getJSONArray("choices")
                    .getJSONObject(0)
                    .getJSONObject("message")
                    .getString("content")
                    .trim()
            }
        }
    }

    fun fallbackReply(prompt: String, environmentSummary: String): String {
        val normalized = prompt.lowercase()
        return when {
            "termux" in normalized -> "我先帮你检查了执行环境。$environmentSummary。下一步建议先确认 Termux、Termux API 和 Shizuku 的安装情况，再执行命令链路。"
            "文件" in prompt || "创建" in prompt || "删除" in prompt || "编辑" in prompt ->
                "当前已接入应用工作区文件操作。你可以在工作台里输入文件名和内容，直接创建、覆盖或删除文件，并把结果写入任务日志。"
            "命令" in prompt || "shell" in normalized || "终端" in prompt ->
                "当前版本支持基础 Shell 命令执行、输出回显、失败重试和任务记录。建议先运行 `pwd`、`ls` 或构建类命令验证链路。"
            else ->
                "我已经进入第二阶段最小可用模式。当前可用能力包括 AI 对话接口接入、环境检测、命令执行、文件操作、任务状态流转和本地持久化。你可以继续给我一个明确目标，我会优先拆成可执行步骤。"
        }
    }

    private fun MessageRole.toWireRole(): String {
        return when (this) {
            MessageRole.USER -> "user"
            MessageRole.ASSISTANT -> "assistant"
            MessageRole.SYSTEM -> "system"
        }
    }
}

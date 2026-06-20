package com.qianqiu.assistant.runtime

import com.qianqiu.assistant.model.CommandResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.concurrent.TimeUnit

class ShellExecutor {
    suspend fun run(command: String, maxAttempts: Int = 2): CommandResult = withContext(Dispatchers.IO) {
        var attempt = 0
        var lastResult = CommandResult(
            command = command,
            exitCode = -1,
            stdout = "",
            stderr = "未执行",
            durationMs = 0L,
            attempts = 0
        )

        while (attempt < maxAttempts) {
            attempt += 1
            lastResult = executeOnce(command, attempt)
            if (lastResult.exitCode == 0) {
                return@withContext lastResult
            }
        }
        lastResult
    }

    private fun executeOnce(command: String, attempt: Int): CommandResult {
        val startedAt = System.currentTimeMillis()
        return try {
            val process = ProcessBuilder("sh", "-c", command).start()
            val finished = process.waitFor(20, TimeUnit.SECONDS)
            if (!finished) {
                process.destroyForcibly()
                return CommandResult(
                    command = command,
                    exitCode = -2,
                    stdout = "",
                    stderr = "命令执行超时，已在 20 秒后终止",
                    durationMs = System.currentTimeMillis() - startedAt,
                    attempts = attempt
                )
            }

            CommandResult(
                command = command,
                exitCode = process.exitValue(),
                stdout = process.inputStream.bufferedReader().readText().trim(),
                stderr = process.errorStream.bufferedReader().readText().trim(),
                durationMs = System.currentTimeMillis() - startedAt,
                attempts = attempt
            )
        } catch (error: Exception) {
            CommandResult(
                command = command,
                exitCode = -1,
                stdout = "",
                stderr = error.message ?: "命令执行失败",
                durationMs = System.currentTimeMillis() - startedAt,
                attempts = attempt
            )
        }
    }
}

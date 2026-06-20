package com.qianqiu.assistant.data

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.qianqiu.assistant.model.AgentTask
import com.qianqiu.assistant.model.AiSettings
import com.qianqiu.assistant.model.ChatMessage
import com.qianqiu.assistant.model.MessageRole
import com.qianqiu.assistant.model.TaskLog
import com.qianqiu.assistant.model.TaskStatus

class AppDatabaseHelper(context: Context) : SQLiteOpenHelper(
    context,
    DATABASE_NAME,
    null,
    DATABASE_VERSION
) {
    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(
            """
            CREATE TABLE messages (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                role TEXT NOT NULL,
                content TEXT NOT NULL,
                created_at INTEGER NOT NULL
            )
            """.trimIndent()
        )
        db.execSQL(
            """
            CREATE TABLE tasks (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                title TEXT NOT NULL,
                detail TEXT NOT NULL,
                status TEXT NOT NULL,
                progress INTEGER NOT NULL,
                current_step TEXT NOT NULL,
                retry_count INTEGER NOT NULL,
                created_at INTEGER NOT NULL,
                updated_at INTEGER NOT NULL
            )
            """.trimIndent()
        )
        db.execSQL(
            """
            CREATE TABLE task_logs (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                task_id INTEGER NOT NULL,
                level TEXT NOT NULL,
                message TEXT NOT NULL,
                created_at INTEGER NOT NULL
            )
            """.trimIndent()
        )
        db.execSQL(
            """
            CREATE TABLE settings (
                key TEXT PRIMARY KEY,
                value TEXT NOT NULL
            )
            """.trimIndent()
        )
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) = Unit

    fun insertMessage(role: MessageRole, content: String, createdAt: Long): Long {
        val values = ContentValues().apply {
            put("role", role.name)
            put("content", content)
            put("created_at", createdAt)
        }
        return writableDatabase.insert("messages", null, values)
    }

    fun getMessages(): List<ChatMessage> {
        val cursor = readableDatabase.query(
            "messages",
            arrayOf("id", "role", "content", "created_at"),
            null,
            null,
            null,
            null,
            "created_at ASC"
        )
        return cursor.use {
            buildList {
                while (it.moveToNext()) {
                    add(
                        ChatMessage(
                            id = it.getLong(0),
                            role = MessageRole.valueOf(it.getString(1)),
                            content = it.getString(2),
                            createdAt = it.getLong(3)
                        )
                    )
                }
            }
        }
    }

    fun insertTask(
        title: String,
        detail: String,
        status: TaskStatus,
        progress: Int,
        currentStep: String,
        retryCount: Int,
        createdAt: Long,
        updatedAt: Long
    ): Long {
        val values = ContentValues().apply {
            put("title", title)
            put("detail", detail)
            put("status", status.name)
            put("progress", progress)
            put("current_step", currentStep)
            put("retry_count", retryCount)
            put("created_at", createdAt)
            put("updated_at", updatedAt)
        }
        return writableDatabase.insert("tasks", null, values)
    }

    fun updateTask(
        id: Long,
        status: TaskStatus,
        progress: Int,
        currentStep: String,
        retryCount: Int,
        detail: String,
        updatedAt: Long
    ) {
        val values = ContentValues().apply {
            put("status", status.name)
            put("progress", progress)
            put("current_step", currentStep)
            put("retry_count", retryCount)
            put("detail", detail)
            put("updated_at", updatedAt)
        }
        writableDatabase.update("tasks", values, "id = ?", arrayOf(id.toString()))
    }

    fun getTasks(): List<AgentTask> {
        val cursor = readableDatabase.query(
            "tasks",
            arrayOf(
                "id",
                "title",
                "detail",
                "status",
                "progress",
                "current_step",
                "retry_count",
                "created_at",
                "updated_at"
            ),
            null,
            null,
            null,
            null,
            "updated_at DESC"
        )
        return cursor.use {
            buildList {
                while (it.moveToNext()) {
                    add(
                        AgentTask(
                            id = it.getLong(0),
                            title = it.getString(1),
                            detail = it.getString(2),
                            status = TaskStatus.valueOf(it.getString(3)),
                            progress = it.getInt(4),
                            currentStep = it.getString(5),
                            retryCount = it.getInt(6),
                            createdAt = it.getLong(7),
                            updatedAt = it.getLong(8)
                        )
                    )
                }
            }
        }
    }

    fun insertTaskLog(taskId: Long, level: String, message: String, createdAt: Long): Long {
        val values = ContentValues().apply {
            put("task_id", taskId)
            put("level", level)
            put("message", message)
            put("created_at", createdAt)
        }
        return writableDatabase.insert("task_logs", null, values)
    }

    fun getTaskLogs(limit: Int = 80): List<TaskLog> {
        val cursor = readableDatabase.query(
            "task_logs",
            arrayOf("id", "task_id", "level", "message", "created_at"),
            null,
            null,
            null,
            null,
            "created_at DESC",
            limit.toString()
        )
        return cursor.use {
            buildList {
                while (it.moveToNext()) {
                    add(
                        TaskLog(
                            id = it.getLong(0),
                            taskId = it.getLong(1),
                            level = it.getString(2),
                            message = it.getString(3),
                            createdAt = it.getLong(4)
                        )
                    )
                }
            }
        }
    }

    fun saveSetting(key: String, value: String) {
        val values = ContentValues().apply {
            put("key", key)
            put("value", value)
        }
        writableDatabase.insertWithOnConflict(
            "settings",
            null,
            values,
            SQLiteDatabase.CONFLICT_REPLACE
        )
    }

    fun loadAiSettings(): AiSettings {
        val data = mutableMapOf<String, String>()
        val cursor = readableDatabase.query(
            "settings",
            arrayOf("key", "value"),
            null,
            null,
            null,
            null,
            null
        )
        cursor.use {
            while (it.moveToNext()) {
                data[it.getString(0)] = it.getString(1)
            }
        }
        return AiSettings(
            baseUrl = data["ai_base_url"] ?: "https://api.openai.com/v1/chat/completions",
            apiKey = data["ai_api_key"].orEmpty(),
            model = data["ai_model"] ?: "gpt-4o-mini"
        )
    }

    companion object {
        private const val DATABASE_NAME = "qianqiu_mvp.db"
        private const val DATABASE_VERSION = 1
    }
}

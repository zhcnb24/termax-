package com.qianqiu.assistant.runtime

import android.content.Context
import com.qianqiu.assistant.model.WorkspaceFile
import java.io.File

class FileWorkspaceManager(context: Context) {
    private val workspaceDir: File = File(context.filesDir, "workspace").apply {
        mkdirs()
    }

    fun createOrUpdateFile(name: String, content: String): WorkspaceFile {
        val safeName = name.trim().ifBlank { "untitled.txt" }
        val target = resolveInsideWorkspace(safeName)
        target.parentFile?.mkdirs()
        target.writeText(content)
        return WorkspaceFile(
            name = target.relativeTo(workspaceDir).path,
            content = content,
            updatedAt = target.lastModified()
        )
    }

    fun deleteFile(name: String): Boolean {
        val target = resolveInsideWorkspace(name.trim())
        return target.exists() && target.delete()
    }

    fun listFiles(): List<WorkspaceFile> {
        return workspaceDir
            .listFiles()
            .orEmpty()
            .filter { it.isFile }
            .sortedByDescending { it.lastModified() }
            .map { file ->
                WorkspaceFile(
                    name = file.name,
                    content = file.readText(),
                    updatedAt = file.lastModified()
                )
            }
    }

    fun workspacePath(): String = workspaceDir.absolutePath

    private fun resolveInsideWorkspace(name: String): File {
        val target = File(workspaceDir, name).canonicalFile
        val root = workspaceDir.canonicalFile
        require(target.path.startsWith(root.path)) {
            "文件路径必须位于工作区内"
        }
        return target
    }
}

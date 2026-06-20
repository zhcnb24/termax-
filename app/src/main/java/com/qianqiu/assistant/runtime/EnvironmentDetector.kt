package com.qianqiu.assistant.runtime

import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import com.qianqiu.assistant.model.EnvironmentStatus

class EnvironmentDetector(private val context: Context) {
    fun detect(): EnvironmentStatus {
        val packageManager = context.packageManager
        val termuxInstalled = isInstalled(packageManager, "com.termux")
        val termuxApiInstalled = isInstalled(packageManager, "com.termux.api")
        val shizukuInstalled = isInstalled(packageManager, "moe.shizuku.privileged.api")
        val preferred = when {
            shizukuInstalled -> "Shizuku"
            termuxApiInstalled -> "Termux API"
            termuxInstalled -> "Intent"
            else -> "内置 Shell"
        }
        return EnvironmentStatus(
            termuxInstalled = termuxInstalled,
            termuxApiInstalled = termuxApiInstalled,
            shizukuInstalled = shizukuInstalled,
            preferredChannel = preferred,
            lastCheckedAt = System.currentTimeMillis()
        )
    }

    private fun isInstalled(packageManager: PackageManager, packageName: String): Boolean {
        return try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                packageManager.getPackageInfo(
                    packageName,
                    PackageManager.PackageInfoFlags.of(0)
                )
            } else {
                @Suppress("DEPRECATION")
                packageManager.getPackageInfo(packageName, 0)
            }
            true
        } catch (_: Exception) {
            false
        }
    }
}

package com.qianqiu.assistant.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val QianQiuDarkColors: ColorScheme = darkColorScheme(
    primary = Color(0xFF7C8CFF),
    onPrimary = Color(0xFF101323),
    secondary = Color(0xFF85D6FF),
    tertiary = Color(0xFFF6A6FF),
    background = Color(0xFF0A0D18),
    onBackground = Color(0xFFF4F6FF),
    surface = Color(0xFF12172A),
    surfaceVariant = Color(0xFF1A2038),
    onSurface = Color(0xFFF4F6FF),
    onSurfaceVariant = Color(0xFFB6BDD6)
)

private val QianQiuLightColors: ColorScheme = lightColorScheme(
    primary = Color(0xFF5363FF),
    secondary = Color(0xFF0072C9),
    tertiary = Color(0xFFB140C6),
    background = Color(0xFFF3F6FF),
    onBackground = Color(0xFF11131A),
    surface = Color(0xFFFFFFFF),
    onSurface = Color(0xFF11131A),
    surfaceVariant = Color(0xFFE6ECFF),
    onSurfaceVariant = Color(0xFF50596F)
)

@Composable
fun TermuxQianQiuTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (darkTheme) QianQiuDarkColors else QianQiuLightColors,
        content = content
    )
}

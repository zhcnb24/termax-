package com.qianqiu.assistant

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.view.WindowCompat
import com.qianqiu.assistant.ui.QianQiuApp
import com.qianqiu.assistant.ui.theme.TermuxQianQiuTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContent {
            TermuxQianQiuTheme {
                QianQiuApp()
            }
        }
    }
}

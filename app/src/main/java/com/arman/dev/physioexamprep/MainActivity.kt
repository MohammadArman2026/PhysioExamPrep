package com.arman.dev.physioexamprep

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.ui.graphics.toArgb
import com.arman.dev.physioexamprep.core.ui.theme.AppCanvas
import com.arman.dev.physioexamprep.core.ui.theme.PhysioExamPrepTheme
import com.arman.dev.physioexamprep.navigation.PhysioExamPrepNavHost
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(
            navigationBarStyle = SystemBarStyle.light(
                AppCanvas.toArgb(),
                AppCanvas.toArgb()
            )
        )
        setContent {
            PhysioExamPrepTheme {
                PhysioExamPrepNavHost()
            }
        }
    }
}
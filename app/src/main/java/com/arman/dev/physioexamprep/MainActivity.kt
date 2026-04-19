package com.arman.dev.physioexamprep
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.compose.rememberNavController
import com.arman.dev.physioexamprep.core.ui.theme.PhysioExamPrepTheme
import com.arman.dev.physioexamprep.navigation.AppNavigationHost

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val navController = rememberNavController()
            PhysioExamPrepTheme {
                AppNavigationHost(
                    navController = navController
                )
            }
        }
    }
}
package com.sentinel.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.collectAsState
import androidx.core.view.WindowCompat
import com.sentinel.app.ui.navigation.SentinelNavHost
import com.sentinel.app.ui.theme.SentinelTheme
import com.sentinel.app.ui.viewmodel.ScanViewModel

class MainActivity : ComponentActivity() {
    private val viewModel: ScanViewModel by viewModels()
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContent {
            val isDarkTheme = viewModel.isDarkMode.collectAsState().value
            SentinelTheme(darkTheme = isDarkTheme) {
                SentinelNavHost(viewModel = viewModel)
            }
        }
    }
}

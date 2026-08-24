package com.sentinel.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.sentinel.app.engine.AuthManager
import com.sentinel.app.ui.viewmodel.ScanViewModel
import kotlinx.coroutines.launch

@Composable
fun SetupScreen(viewModel: ScanViewModel, onSetupComplete: () -> Unit) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    
    var isLoggingIn by remember { mutableStateOf(false) }
    var loginError by remember { mutableStateOf<String?>(null) }

    val darkColors = darkColorScheme(
        background = Color(0xFF121212),
        surface = Color(0xFF1E1E1E),
        onBackground = Color.White,
        onSurface = Color.White,
        primary = Color(0xFF4CAF50)
    )

    MaterialTheme(colorScheme = darkColors) {
        Surface(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier.fillMaxSize().padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text("🛡️", style = MaterialTheme.typography.displayLarge)
                Spacer(Modifier.height(16.dp))
                Text(
                    text = "Sentinel",
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    text = "On-Device & Cloud Code Guardian",
                    color = Color.Gray,
                    style = MaterialTheme.typography.bodyMedium
                )
                
                Spacer(Modifier.height(64.dp))

                if (isLoggingIn) {
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                    Spacer(Modifier.height(16.dp))
                    Text("Authenticating...", color = Color.LightGray)
                } else {
                    Button(
                        onClick = {
                            isLoggingIn = true
                            loginError = null
                            coroutineScope.launch {
                                val success = AuthManager.signInWithGoogle(context)
                                isLoggingIn = false
                                if (success) {
                                    viewModel.setSetupComplete()
                                    onSetupComplete()
                                } else {
                                    loginError = "Authentication failed. Please try again."
                                }
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth().height(56.dp)
                    ) {
                        Text("Sign in with Google", color = Color.Black, fontWeight = FontWeight.Bold)
                    }
                    if (loginError != null) {
                        Spacer(Modifier.height(8.dp))
                        Text(loginError!!, color = Color.Red, style = MaterialTheme.typography.bodySmall)
                    }
                }
            }
        }
    }
}

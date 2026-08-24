package com.sentinel.app.ui.screens
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.sentinel.app.ui.viewmodel.ScanViewModel
@Composable
fun SetupScreen(viewModel: ScanViewModel, onSetupComplete: () -> Unit) {
    var path by remember { mutableStateOf("") }
    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("🛡️", style = MaterialTheme.typography.displayLarge)
        Spacer(Modifier.height(16.dp))
        Text("Welcome to Sentinel", style = MaterialTheme.typography.headlineLarge)
        Spacer(Modifier.height(8.dp))
        Text("Provide the path to your quantized Phi-3 Mini model file to begin.", color = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(Modifier.height(32.dp))
        OutlinedTextField(
            value = path,
            onValueChange = { path = it },
            label = { Text("Model Path") },
            placeholder = { Text("/storage/emulated/0/Documents/phi3.bin") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(24.dp))
        Button(
            onClick = {
                viewModel.setModelPath(path)
                onSetupComplete()
            },
            modifier = Modifier.fillMaxWidth().height(56.dp)
        ) {
            Text("Continue")
        }
    }
}

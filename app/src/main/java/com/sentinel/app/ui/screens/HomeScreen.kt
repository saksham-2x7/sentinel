package com.sentinel.app.ui.screens
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.sentinel.app.domain.ScanState
import com.sentinel.app.ui.components.ScanningAnimation
import com.sentinel.app.ui.viewmodel.ScanViewModel
@Composable
fun HomeScreen(viewModel: ScanViewModel, onScanComplete: (Long) -> Unit, onHistoryClick: () -> Unit) {
    val state by viewModel.scanState.collectAsState()
    var code by remember { mutableStateOf("") }
    var language by remember { mutableStateOf("Kotlin") }
    LaunchedEffect(state) {
        if (state is ScanState.Success) {
            val id = (state as ScanState.Success).result.id
            if (id > 0) onScanComplete(id)
        }
    }
    Scaffold(
        topBar = {
            @OptIn(ExperimentalMaterial3Api::class)
            TopAppBar(
                title = { Text("Sentinel") },
                actions = { TextButton(onClick = onHistoryClick) { Text("History") } }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (state is ScanState.Scanning) {
                Spacer(Modifier.weight(1f))
                ScanningAnimation(
                    progress = (state as ScanState.Scanning).progress,
                    statusText = (state as ScanState.Scanning).statusText
                )
                Spacer(Modifier.weight(1f))
            } else {
                OutlinedTextField(
                    value = language,
                    onValueChange = { language = it },
                    label = { Text("Language (e.g. Kotlin, Python)") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(16.dp))
                OutlinedTextField(
                    value = code,
                    onValueChange = { code = it },
                    label = { Text("Paste Code Here") },
                    modifier = Modifier.fillMaxWidth().weight(1f)
                )
                Spacer(Modifier.height(16.dp))
                Button(
                    onClick = { viewModel.scanCode(code, language) },
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    enabled = code.isNotBlank()
                ) {
                    Text("Scan Code")
                }
            }
            if (state is ScanState.Error) {
                Spacer(Modifier.height(16.dp))
                Text((state as ScanState.Error).message, color = MaterialTheme.colorScheme.error)
            }
        }
    }
}

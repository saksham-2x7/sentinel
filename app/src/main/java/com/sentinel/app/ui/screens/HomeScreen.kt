package com.sentinel.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.sentinel.app.domain.ScanState
import com.sentinel.app.ui.components.ScanningAnimation
import com.sentinel.app.ui.viewmodel.ScanViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(viewModel: ScanViewModel, onScanComplete: (Long) -> Unit, onHistoryClick: () -> Unit, onSettingsClick: () -> Unit) {
    val state by viewModel.scanState.collectAsState()
    var code by remember { mutableStateOf("") }
    var useCloud by remember { mutableStateOf(false) }

    val languages = listOf("Python", "JavaScript", "Java", "Kotlin", "C++", "Go", "Rust", "TypeScript", "PHP", "Ruby", "Swift", "C#", "HTML/CSS", "SQL", "Dockerfile", "YAML")
    var expanded by remember { mutableStateOf(false) }
    var selectedLanguage by remember { mutableStateOf(languages[0]) }

    LaunchedEffect(state) {
        if (state is ScanState.Success) {
            val id = (state as ScanState.Success).result.id
            if (id > 0) onScanComplete(id)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Sentinel") },
                actions = { 
                    TextButton(onClick = onHistoryClick) { Text("History") } 
                    IconButton(onClick = onSettingsClick) {
                        Icon(Icons.Default.Settings, contentDescription = "Settings")
                    }
                }
            )
        },
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    icon = { Icon(Icons.Default.PhoneAndroid, contentDescription = "Offline AI") },
                    label = { Text("Offline AI") },
                    selected = !useCloud,
                    onClick = { useCloud = false }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Cloud, contentDescription = "Cloud AI") },
                    label = { Text("Cloud Agent") },
                    selected = useCloud,
                    onClick = { useCloud = true }
                )
            }
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
                Text(
                    text = if (useCloud) "Using Gemini Cloud Agent API" else "Using Local MediaPipe AI",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(Modifier.height(16.dp))

                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = selectedLanguage,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Select Language") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                        colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
                        modifier = Modifier.menuAnchor().fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        languages.forEach { lang ->
                            DropdownMenuItem(
                                text = { Text(lang) },
                                onClick = {
                                    selectedLanguage = lang
                                    expanded = false
                                }
                            )
                        }
                    }
                }

                Spacer(Modifier.height(16.dp))
                OutlinedTextField(
                    value = code,
                    onValueChange = { code = it },
                    label = { Text("Paste Code Here") },
                    modifier = Modifier.fillMaxWidth().weight(1f)
                )
                Spacer(Modifier.height(16.dp))
                Button(
                    onClick = { viewModel.scanCode(code, selectedLanguage, useCloud = useCloud) },
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    enabled = code.isNotBlank()
                ) {
                    Text(if (useCloud) "Scan via Cloud" else "Scan Locally")
                }
            }
            if (state is ScanState.Error) {
                Spacer(Modifier.height(16.dp))
                Text((state as ScanState.Error).message, color = MaterialTheme.colorScheme.error)
            }
        }
    }
}

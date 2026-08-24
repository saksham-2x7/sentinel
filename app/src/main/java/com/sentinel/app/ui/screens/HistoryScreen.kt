package com.sentinel.app.ui.screens
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.sentinel.app.ui.viewmodel.ScanViewModel
@Composable
fun HistoryScreen(viewModel: ScanViewModel, onBack: () -> Unit, onScanClick: (Long) -> Unit) {
    val history by viewModel.scanHistory.collectAsState(initial = emptyList())
    Scaffold(
        topBar = {
            @OptIn(ExperimentalMaterial3Api::class)
            TopAppBar(
                title = { Text("History") },
                navigationIcon = {
                    IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, "Back") }
                }
            )
        }
    ) { padding ->
        LazyColumn(modifier = Modifier.padding(padding).fillMaxSize()) {
            items(history) { scan ->
                ListItem(
                    headlineContent = { Text(scan.fileName) },
                    supportingContent = { Text("Score: ${scan.securityScore} - ${scan.language}") },
                    modifier = Modifier.clickable { onScanClick(scan.id) }
                )
                HorizontalDivider()
            }
        }
    }
}

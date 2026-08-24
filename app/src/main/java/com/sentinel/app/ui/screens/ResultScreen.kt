package com.sentinel.app.ui.screens
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.sentinel.app.domain.ScanResult
import com.sentinel.app.ui.components.FindingCard
import com.sentinel.app.ui.components.SecurityScoreRing
import com.sentinel.app.ui.viewmodel.ScanViewModel
@Composable
fun ResultScreen(scanId: Long, viewModel: ScanViewModel, onBack: () -> Unit, onNewScan: () -> Unit) {
    var result by remember { mutableStateOf<ScanResult?>(null) }
    LaunchedEffect(scanId) {
        result = viewModel.getScanById(scanId)
        if (result == null && viewModel.scanState.value is com.sentinel.app.domain.ScanState.Success) {
            result = (viewModel.scanState.value as com.sentinel.app.domain.ScanState.Success).result
        }
    }
    Scaffold(
        topBar = {
            @OptIn(ExperimentalMaterial3Api::class)
            TopAppBar(
                title = { Text("Scan Results") },
                navigationIcon = {
                    IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, "Back") }
                }
            )
        },
        bottomBar = {
            Box(Modifier.padding(16.dp)) {
                Button(onClick = onNewScan, modifier = Modifier.fillMaxWidth().height(56.dp)) {
                    Text("New Scan")
                }
            }
        }
    ) { padding ->
        if (result == null) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
            return@Scaffold
        }
        val r = result!!
        LazyColumn(modifier = Modifier.padding(padding).fillMaxSize().padding(horizontal = 16.dp)) {
            item {
                Box(Modifier.fillMaxWidth().padding(vertical = 24.dp), contentAlignment = Alignment.Center) {
                    SecurityScoreRing(score = r.securityScore)
                }
                Spacer(Modifier.height(16.dp))
                if (r.findings.isEmpty()) {
                    Text("No vulnerabilities found! Great job.", style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.primary)
                }
            }
            items(r.findings) { finding ->
                FindingCard(finding = finding, modifier = Modifier.padding(vertical = 8.dp))
            }
        }
    }
}

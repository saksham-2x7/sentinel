package com.sentinel.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.sentinel.app.domain.ScanResult
import com.sentinel.app.ui.components.FindingCard
import com.sentinel.app.ui.components.ScoreRing
import com.sentinel.app.ui.viewmodel.ScanViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResultScreen(scanId: Long, viewModel: ScanViewModel, onBack: () -> Unit, onNewScan: () -> Unit) {
    var result by remember { mutableStateOf<ScanResult?>(null) }
    
    LaunchedEffect(scanId) {
        result = viewModel.getScanById(scanId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Scan Report") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        if (result == null) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            val r = result!!
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(padding).padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    Spacer(Modifier.height(16.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("Risk Score", style = MaterialTheme.typography.titleMedium, color = Color.Gray)
                            Text(if (r.isSecure) "Secure" else "At Risk", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
                        }
                        ScoreRing(score = r.securityScore, size = 100.dp)
                    }
                }
                
                // NEW: General Output Box
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFF1E1E1E))
                            .border(1.dp, Color.DarkGray, RoundedCornerShape(12.dp))
                            .padding(16.dp)
                    ) {
                        Text("📝 General Output", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = Color(0xFF42A5F5))
                        Spacer(Modifier.height(8.dp))
                        Text(
                            text = if (r.isSecure) 
                                "Great job! Your ${r.language} code has passed the security scan with a perfect score. No vulnerabilities were detected." 
                            else 
                                "Sentinel analyzed your ${r.language} code and detected ${r.totalFindings} vulnerabilities that need your attention. Please review the highlighted issues below.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.LightGray
                        )
                    }
                }

                if (r.isSecure) {
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 32.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                        ) {
                            Column(
                                modifier = Modifier.padding(24.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text("🎉", style = MaterialTheme.typography.displayMedium)
                                Spacer(Modifier.height(16.dp))
                                Text("No Vulnerabilities Found!", style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.onPrimaryContainer)
                            }
                        }
                    }
                } else {
                    item {
                        Text("Detailed Findings", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                    }
                    items(r.findings) { finding ->
                        FindingCard(finding = finding)
                    }
                }
                item {
                    Spacer(Modifier.height(16.dp))
                    Button(
                        onClick = onNewScan,
                        modifier = Modifier.fillMaxWidth().height(56.dp)
                    ) {
                        Text("New Scan")
                    }
                    Spacer(Modifier.height(32.dp))
                }
            }
        }
    }
}

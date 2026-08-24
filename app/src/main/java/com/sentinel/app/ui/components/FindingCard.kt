package com.sentinel.app.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.sentinel.app.domain.Finding
import com.sentinel.app.domain.Severity
import com.sentinel.app.ui.theme.*

@Composable
fun FindingCard(finding: Finding, modifier: Modifier = Modifier) {
    var expanded by remember { mutableStateOf(false) }
    val clipboard = LocalClipboardManager.current
    
    val (severityColor, severityBg, severityLabel) = when (finding.severity) {
        Severity.HIGH -> Triple(SeverityHigh, SeverityHighContainer, "HIGH")
        Severity.MEDIUM -> Triple(SeverityMedium, SeverityMediumContainer, "MED")
        Severity.LOW -> Triple(SeverityLow, SeverityLowContainer, "LOW")
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFF1E1E1E))
            .border(1.dp, Color.DarkGray, RoundedCornerShape(16.dp))
            .clickable { expanded = !expanded }
    ) {
        Box(modifier = Modifier.fillMaxWidth().height(3.dp).background(severityColor))
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                    Text(finding.type.emoji(), style = MaterialTheme.typography.titleLarge)
                    Spacer(Modifier.width(10.dp))
                    Column {
                        // Title in BLUE
                        Text(finding.title, style = MaterialTheme.typography.titleMedium, color = Color(0xFF42A5F5), fontWeight = FontWeight.Bold, maxLines = 2)
                        if (finding.line != null) {
                            Text("Line ${finding.line}", style = MaterialTheme.typography.labelSmall, color = Color.LightGray)
                        }
                    }
                }
                Spacer(Modifier.width(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .background(severityBg, RoundedCornerShape(6.dp))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(severityLabel, style = MaterialTheme.typography.labelSmall, color = severityColor)
                    }
                    Spacer(Modifier.width(8.dp))
                    Icon(
                        if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                        contentDescription = null,
                        tint = Color.Gray,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
            AnimatedVisibility(
                visible = expanded,
                enter = expandVertically(),
                exit = shrinkVertically()
            ) {
                Column(modifier = Modifier.padding(top = 14.dp)) {
                    HorizontalDivider(color = Color.DarkGray, thickness = 0.5.dp)
                    Spacer(Modifier.height(12.dp))
                    
                    Text("❌ ERROR IN CODE", style = MaterialTheme.typography.labelSmall, color = Color(0xFFFF5252))
                    Spacer(Modifier.height(4.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFF2A1B1B), RoundedCornerShape(8.dp))
                            .border(1.dp, Color(0xFF4A2B2B), RoundedCornerShape(8.dp))
                            .padding(12.dp)
                    ) {
                        // Vulnerable code in RED
                        Text(
                            text = finding.description, 
                            style = MaterialTheme.typography.bodyMedium, 
                            color = Color(0xFFFF5252),
                            fontFamily = FontFamily.Monospace
                        )
                    }
                    
                    Spacer(Modifier.height(14.dp))
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("✅ CORRECTED CODE", style = MaterialTheme.typography.labelSmall, color = Color(0xFF69F0AE))
                        IconButton(
                            onClick = { clipboard.setText(AnnotatedString(finding.fix)) },
                            modifier = Modifier.size(28.dp)
                        ) {
                            Icon(Icons.Default.ContentCopy, contentDescription = "Copy fix", tint = Color.Gray, modifier = Modifier.size(14.dp))
                        }
                    }
                    Spacer(Modifier.height(4.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFF1B2A1E), RoundedCornerShape(8.dp))
                            .border(1.dp, Color(0xFF2B4A33), RoundedCornerShape(8.dp))
                            .padding(12.dp)
                    ) {
                        // Corrected code in GREEN
                        Text(
                            text = finding.fix, 
                            style = MaterialTheme.typography.bodyMedium, 
                            color = Color(0xFF69F0AE),
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }
            }
        }
    }
}

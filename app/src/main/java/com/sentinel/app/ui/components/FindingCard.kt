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
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
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
            .background(CardBackground)
            .border(1.dp, CardBorder, RoundedCornerShape(16.dp))
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
                        Text(finding.title, style = MaterialTheme.typography.titleMedium, color = TextPrimary, maxLines = 2)
                        if (finding.line != null) {
                            Text("Line ${finding.line}", style = MaterialTheme.typography.labelSmall, color = TextSecondary)
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
                        tint = TextSecondary,
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
                    HorizontalDivider(color = CardBorder, thickness = 0.5.dp)
                    Spacer(Modifier.height(12.dp))
                    Text("WHAT'S WRONG", style = MaterialTheme.typography.labelSmall, color = TextTertiary)
                    Spacer(Modifier.height(4.dp))
                    Text(finding.description, style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
                    Spacer(Modifier.height(14.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("HOW TO FIX", style = MaterialTheme.typography.labelSmall, color = TextTertiary)
                        IconButton(
                            onClick = { clipboard.setText(AnnotatedString(finding.fix)) },
                            modifier = Modifier.size(28.dp)
                        ) {
                            Icon(Icons.Default.ContentCopy, contentDescription = "Copy fix", tint = TextSecondary, modifier = Modifier.size(14.dp))
                        }
                    }
                    Spacer(Modifier.height(4.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(SurfaceVariant, RoundedCornerShape(8.dp))
                            .padding(12.dp)
                    ) {
                        Text(finding.fix, style = MaterialTheme.typography.bodyMedium, color = TextPrimary)
                    }
                }
            }
        }
    }
}

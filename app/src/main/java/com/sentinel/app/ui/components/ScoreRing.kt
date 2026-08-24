package com.sentinel.app.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun ScoreRing(score: Int, size: Dp = 100.dp) {
    val color = when {
        score >= 80 -> Color(0xFF69F0AE) // Green
        score >= 50 -> Color(0xFFFFD54F) // Yellow
        else -> Color(0xFFFF5252) // Red
    }
    
    Box(modifier = Modifier.size(size), contentAlignment = Alignment.Center) {
        Canvas(modifier = Modifier.size(size)) {
            val strokeWidth = 8.dp.toPx()
            drawArc(
                color = Color.DarkGray,
                startAngle = 0f,
                sweepAngle = 360f,
                useCenter = false,
                style = Stroke(strokeWidth, cap = StrokeCap.Round)
            )
            drawArc(
                color = color,
                startAngle = -90f,
                sweepAngle = 360f * (score / 100f),
                useCenter = false,
                style = Stroke(strokeWidth, cap = StrokeCap.Round)
            )
        }
        Text(
            text = score.toString(),
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
    }
}

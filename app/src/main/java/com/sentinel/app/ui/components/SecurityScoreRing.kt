package com.sentinel.app.ui.components
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sentinel.app.ui.theme.*
@Composable
fun SecurityScoreRing(
    score: Int,
    modifier: Modifier = Modifier,
    size: Dp = 160.dp,
    strokeWidth: Dp = 14.dp
) {
    val animatedScore by animateFloatAsState(
        targetValue = score.toFloat(),
        animationSpec = tween(durationMillis = 1200, easing = EaseOutCubic),
        label = "score"
    )
    val ringColor = when {
        score >= 80 -> SeverityNone
        score >= 60 -> SeverityLow
        score >= 40 -> SeverityMedium
        else -> SeverityHigh
    }
    val label = when {
        score >= 80 -> "SECURE"
        score >= 60 -> "FAIR"
        score >= 40 -> "AT RISK"
        else -> "CRITICAL"
    }
    Box(modifier = modifier.size(size), contentAlignment = Alignment.Center) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val strokePx = strokeWidth.toPx()
            val inset = strokePx / 2
            val arcSize = Size(this.size.width - strokePx, this.size.height - strokePx)
            val topLeft = Offset(inset, inset)
            drawArc(
                color = Color(0xFF2A2A2A),
                startAngle = -90f,
                sweepAngle = 360f,
                useCenter = false,
                topLeft = topLeft,
                size = arcSize,
                style = Stroke(width = strokePx, cap = StrokeCap.Round)
            )
            drawArc(
                color = ringColor,
                startAngle = -90f,
                sweepAngle = 360f * (animatedScore / 100f),
                useCenter = false,
                topLeft = topLeft,
                size = arcSize,
                style = Stroke(width = strokePx, cap = StrokeCap.Round)
            )
        }
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = animatedScore.toInt().toString(),
                style = MaterialTheme.typography.displayMedium.copy(fontSize = 44.sp),
                color = ringColor
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = ringColor
            )
        }
    }
}

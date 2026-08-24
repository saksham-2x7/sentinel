package com.sentinel.app.ui.components
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.sentinel.app.ui.theme.SentinelRed
import com.sentinel.app.ui.theme.TextSecondary
@Composable
fun ScanningAnimation(progress: Float, statusText: String, modifier: Modifier = Modifier) {
    val infiniteTransition = rememberInfiniteTransition(label = "scan")
    val pulse by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(800, easing = EaseInOutSine), RepeatMode.Reverse),
        label = "pulse"
    )
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        Text(
            text = "🛡️",
            style = MaterialTheme.typography.displayLarge,
            modifier = Modifier.alpha(pulse)
        )
        Box(
            modifier = Modifier
                .fillMaxWidth(0.8f)
                .height(4.dp)
                .clip(RoundedCornerShape(2.dp))
                .background(androidx.compose.ui.graphics.Color(0xFF2A2A2A))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(progress.coerceIn(0f, 1f))
                    .clip(RoundedCornerShape(2.dp))
                    .background(SentinelRed)
            )
        }
        Text(
            statusText,
            style = MaterialTheme.typography.bodyMedium,
            color = TextSecondary
        )
    }
}

package com.sentinel.app.ui.theme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
private val DarkColorScheme = darkColorScheme(
    primary = SentinelRed,
    onPrimary = Color.White,
    primaryContainer = SentinelRedDark,
    onPrimaryContainer = Color.White,
    secondary = Color(0xFF8E8E93),
    onSecondary = Color.White,
    background = Background,
    onBackground = TextPrimary,
    surface = Surface,
    onSurface = TextPrimary,
    surfaceVariant = SurfaceVariant,
    onSurfaceVariant = TextSecondary,
    outline = CardBorder,
    error = SeverityHigh
)
@Composable
fun SentinelTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = SentinelTypography,
        content = content
    )
}

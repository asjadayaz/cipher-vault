package com.ciphervault.app.ui.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val ObsidianBackground = Color(0xFF090C10)
val ObsidianSurface = Color(0xFF131822)
val ObsidianSurfaceVariant = Color(0xFF1C2333)

val EmeraldPrimary = Color(0xFF00E676)
val EmeraldOnPrimary = Color(0xFF00381B)
val EmeraldContainer = Color(0xFF00522B)

val GoldAccent = Color(0xFFFFD700)
val GoldContainer = Color(0xFF4A3F00)

val TextPrimary = Color(0xFFE6EDF3)
val TextSecondary = Color(0xFF8B949E)
val ErrorRed = Color(0xFFFF5252)

private val CipherColorScheme: ColorScheme = darkColorScheme(
    primary = EmeraldPrimary,
    onPrimary = EmeraldOnPrimary,
    primaryContainer = EmeraldContainer,
    secondary = GoldAccent,
    secondaryContainer = GoldContainer,
    background = ObsidianBackground,
    surface = ObsidianSurface,
    surfaceVariant = ObsidianSurfaceVariant,
    onBackground = TextPrimary,
    onSurface = TextPrimary,
    onSurfaceVariant = TextSecondary,
    error = ErrorRed
)

@Composable
fun CipherVaultTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = CipherColorScheme,
        typography = Typography(),
        content = content
    )
}

package at.phman.loeweremote.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
    primary = LoeweAccent,
    onPrimary = LoeweTextPrimary,
    background = LoeweBgDark,
    onBackground = LoeweTextPrimary,
    surface = LoeweSurfaceDark,
    onSurface = LoeweTextPrimary,
    surfaceVariant = LoeweSurfaceElevated,
    onSurfaceVariant = LoeweTextSecondary,
    outline = LoeweBorder,
    error = LoeweStatusError
)

@Composable
fun LoeweRemoteTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        content = content
    )
}

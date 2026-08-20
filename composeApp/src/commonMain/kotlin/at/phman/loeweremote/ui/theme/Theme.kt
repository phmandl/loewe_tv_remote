package at.phman.loeweremote.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

private val AnthropicGreekColorScheme = darkColorScheme(
    primary = AnthropicTerracotta,
    onPrimary = AnthropicParchment,
    background = AnthropicBg,
    onBackground = AnthropicParchment,
    surface = AnthropicSurfaceDark,
    onSurface = AnthropicParchment,
    surfaceVariant = AnthropicSurfaceElevated,
    onSurfaceVariant = AnthropicSand,
    outline = AnthropicBorder,
    error = GreekStatusError
)

private val AnthropicGreekTypography = Typography(
    displayLarge = TextStyle(
        fontFamily = FontFamily.Serif,
        fontWeight = FontWeight.Normal,
        fontSize = 32.sp,
        color = AnthropicParchment
    ),
    titleLarge = TextStyle(
        fontFamily = FontFamily.Serif,
        fontWeight = FontWeight.Medium,
        fontSize = 20.sp,
        color = AnthropicParchment
    ),
    titleMedium = TextStyle(
        fontFamily = FontFamily.Serif,
        fontWeight = FontWeight.Medium,
        fontSize = 16.sp,
        color = AnthropicParchment
    ),
    bodyLarge = TextStyle(
        fontFamily = FontFamily.Serif,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        color = AnthropicParchment
    ),
    bodyMedium = TextStyle(
        fontFamily = FontFamily.Serif,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp,
        color = AnthropicSand
    ),
    labelLarge = TextStyle(
        fontFamily = FontFamily.Serif,
        fontWeight = FontWeight.SemiBold,
        fontSize = 13.sp,
        color = AnthropicParchment
    )
)

@Composable
fun LoeweRemoteTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = AnthropicGreekColorScheme,
        typography = AnthropicGreekTypography,
        content = content
    )
}


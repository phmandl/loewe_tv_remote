package at.phman.loeweremote.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import at.phman.loeweremote.ui.theme.AnthropicBorder
import at.phman.loeweremote.ui.theme.AnthropicBorderActive
import at.phman.loeweremote.ui.theme.AnthropicParchment
import at.phman.loeweremote.ui.theme.AnthropicSurfaceElevated

@Composable
fun RemoteButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    label: String? = null,
    textColor: Color = AnthropicParchment,
    backgroundColor: Color = AnthropicSurfaceElevated,
    borderColor: Color = AnthropicBorder,
    shape: Shape = RoundedCornerShape(14.dp),
    minSize: Dp = 48.dp,
    fontSize: TextUnit = 14.sp,
    fontFamily: FontFamily = FontFamily.Serif,
    enabled: Boolean = true,
    content: (@Composable () -> Unit)? = null
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(if (isPressed) 0.94f else 1.0f, label = "button_scale")

    val bgGradient = if (isPressed) {
        Brush.verticalGradient(
            colors = listOf(
                backgroundColor.copy(alpha = 0.65f),
                backgroundColor.copy(alpha = 0.85f)
            )
        )
    } else {
        Brush.verticalGradient(
            colors = listOf(
                backgroundColor.copy(alpha = 0.95f),
                backgroundColor
            )
        )
    }

    Box(
        modifier = modifier
            .scale(scale)
            .defaultMinSize(minWidth = minSize, minHeight = minSize)
            .clip(shape)
            .background(bgGradient)
            .border(
                width = 1.dp,
                color = if (isPressed) AnthropicBorderActive else borderColor,
                shape = shape
            )
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                enabled = enabled,
                onClick = onClick
            )
            .padding(6.dp),
        contentAlignment = Alignment.Center
    ) {
        if (content != null) {
            content()
        } else if (label != null) {
            Text(
                text = label,
                color = textColor,
                fontSize = fontSize,
                fontWeight = FontWeight.SemiBold,
                fontFamily = fontFamily,
                letterSpacing = 0.5.sp,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun CircularRemoteButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    label: String? = null,
    textColor: Color = AnthropicParchment,
    backgroundColor: Color = AnthropicSurfaceElevated,
    borderColor: Color = AnthropicBorder,
    size: Dp = 48.dp,
    fontSize: TextUnit = 14.sp,
    fontFamily: FontFamily = FontFamily.Serif,
    enabled: Boolean = true,
    content: (@Composable () -> Unit)? = null
) {
    RemoteButton(
        onClick = onClick,
        modifier = modifier,
        label = label,
        textColor = textColor,
        backgroundColor = backgroundColor,
        borderColor = borderColor,
        shape = CircleShape,
        minSize = size,
        fontSize = fontSize,
        fontFamily = fontFamily,
        enabled = enabled,
        content = content
    )
}

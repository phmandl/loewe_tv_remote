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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import at.phman.loeweremote.ui.theme.LoeweBorder
import at.phman.loeweremote.ui.theme.LoeweBorderActive
import at.phman.loeweremote.ui.theme.LoeweSurfaceDark
import at.phman.loeweremote.ui.theme.LoeweSurfaceElevated
import at.phman.loeweremote.ui.theme.LoeweTextPrimary

@Composable
fun RemoteButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    label: String? = null,
    textColor: Color = LoeweTextPrimary,
    backgroundColor: Color = LoeweSurfaceElevated,
    borderColor: Color = LoeweBorder,
    shape: Shape = RoundedCornerShape(16.dp),
    minSize: Dp = 54.dp,
    enabled: Boolean = true,
    content: (@Composable () -> Unit)? = null
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(if (isPressed) 0.93f else 1.0f, label = "button_scale")

    val bgGradient = if (isPressed) {
        Brush.verticalGradient(
            colors = listOf(
                backgroundColor.copy(alpha = 0.7f),
                backgroundColor.copy(alpha = 0.9f)
            )
        )
    } else {
        Brush.verticalGradient(
            colors = listOf(
                backgroundColor,
                backgroundColor.copy(alpha = 0.85f)
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
                color = if (isPressed) LoeweBorderActive else borderColor,
                shape = shape
            )
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                enabled = enabled,
                onClick = onClick
            )
            .padding(8.dp),
        contentAlignment = Alignment.Center
    ) {
        if (content != null) {
            content()
        } else if (label != null) {
            Text(
                text = label,
                color = textColor,
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
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
    textColor: Color = LoeweTextPrimary,
    backgroundColor: Color = LoeweSurfaceElevated,
    borderColor: Color = LoeweBorder,
    size: Dp = 54.dp,
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
        enabled = enabled,
        content = content
    )
}

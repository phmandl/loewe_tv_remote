package at.phman.loeweremote.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import at.phman.loeweremote.model.LoeweKey
import at.phman.loeweremote.ui.theme.AnthropicBorder
import at.phman.loeweremote.ui.theme.AnthropicGoldBorder
import at.phman.loeweremote.ui.theme.AnthropicParchment
import at.phman.loeweremote.ui.theme.AnthropicSand
import at.phman.loeweremote.ui.theme.AnthropicSurfaceElevated
import at.phman.loeweremote.ui.theme.AnthropicTerracotta
import at.phman.loeweremote.ui.theme.AnthropicTerracottaBg

@Composable
fun TopControls(
    onKeyClick: (LoeweKey) -> Unit,
    modifier: Modifier = Modifier,
    buttonSize: Dp = 56.dp
) {
    val fontSize = (buttonSize.value * 0.22f).coerceIn(11.5f, 14.5f).sp
    val powerIconSize = (buttonSize * 0.44f).coerceIn(20.dp, 26.dp)

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 2.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Power Button (Anthropic Terracotta Seal)
        RemoteButton(
            onClick = { onKeyClick(LoeweKey.POWER) },
            backgroundColor = AnthropicTerracottaBg,
            borderColor = AnthropicTerracotta.copy(alpha = 0.85f),
            shape = CircleShape,
            minSize = buttonSize,
            modifier = Modifier.size(buttonSize)
        ) {
            PowerIcon(color = AnthropicTerracotta, size = powerIconSize)
        }

        // Info Button
        RemoteButton(
            onClick = { onKeyClick(LoeweKey.INFO) },
            backgroundColor = AnthropicSurfaceElevated,
            borderColor = AnthropicBorder,
            shape = CircleShape,
            minSize = buttonSize,
            modifier = Modifier.size(buttonSize)
        ) {
            Text(
                text = "INFO",
                color = AnthropicSand,
                fontSize = fontSize,
                fontFamily = FontFamily.Serif,
                fontWeight = FontWeight.SemiBold,
                letterSpacing = 0.5.sp,
                maxLines = 1,
                softWrap = false,
                textAlign = TextAlign.Center
            )
        }

        // Menu Button
        RemoteButton(
            onClick = { onKeyClick(LoeweKey.MENU) },
            backgroundColor = AnthropicSurfaceElevated,
            borderColor = AnthropicBorder,
            shape = CircleShape,
            minSize = buttonSize,
            modifier = Modifier.size(buttonSize)
        ) {
            Text(
                text = "MENU",
                color = AnthropicSand,
                fontSize = fontSize,
                fontFamily = FontFamily.Serif,
                fontWeight = FontWeight.SemiBold,
                letterSpacing = 0.5.sp,
                maxLines = 1,
                softWrap = false,
                textAlign = TextAlign.Center
            )
        }

        // Mute Button
        RemoteButton(
            onClick = { onKeyClick(LoeweKey.MUTE) },
            backgroundColor = AnthropicSurfaceElevated,
            borderColor = AnthropicBorder,
            shape = CircleShape,
            minSize = buttonSize,
            modifier = Modifier.size(buttonSize)
        ) {
            Text(
                text = "MUTE",
                color = AnthropicSand,
                fontSize = fontSize,
                fontFamily = FontFamily.Serif,
                fontWeight = FontWeight.SemiBold,
                letterSpacing = 0.5.sp,
                maxLines = 1,
                softWrap = false,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun PowerIcon(
    color: Color = AnthropicTerracotta,
    size: Dp = 18.dp
) {
    Canvas(modifier = Modifier.size(size)) {
        val strokeWidth = 2.dp.toPx()
        val diameter = size.toPx()
        val radius = diameter / 2f
        val pad = strokeWidth / 2f

        // Standard IEC Power Symbol: Gap at the top (-90° / 12 o'clock)
        drawArc(
            color = color,
            startAngle = -55f,
            sweepAngle = 290f,
            useCenter = false,
            topLeft = Offset(pad, pad),
            size = Size(diameter - strokeWidth, diameter - strokeWidth),
            style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
        )

        // Vertical power line centered inside the top gap
        drawLine(
            color = color,
            start = Offset(radius, 0f),
            end = Offset(radius, radius * 0.85f),
            strokeWidth = strokeWidth,
            cap = StrokeCap.Round
        )
    }
}


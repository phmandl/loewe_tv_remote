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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import at.phman.loeweremote.model.LoeweKey
import at.phman.loeweremote.ui.theme.LoeweBorder
import at.phman.loeweremote.ui.theme.LoewePowerRed
import at.phman.loeweremote.ui.theme.LoewePowerRedBg
import at.phman.loeweremote.ui.theme.LoeweTextSecondary

@Composable
fun TopControls(
    onKeyClick: (LoeweKey) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Power Button (Custom Canvas Icon)
        RemoteButton(
            onClick = { onKeyClick(LoeweKey.POWER) },
            backgroundColor = LoewePowerRedBg,
            borderColor = LoewePowerRed.copy(alpha = 0.6f),
            shape = CircleShape,
            minSize = 54.dp,
            modifier = Modifier.size(54.dp)
        ) {
            PowerIcon(color = LoewePowerRed, size = 20.dp)
        }

        // Info Button
        RemoteButton(
            onClick = { onKeyClick(LoeweKey.INFO) },
            shape = CircleShape,
            minSize = 54.dp,
            modifier = Modifier.size(54.dp)
        ) {
            Text(
                text = "INFO",
                color = LoeweTextSecondary,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                softWrap = false,
                textAlign = TextAlign.Center
            )
        }

        // Menu Button (Ensures "MENU" is never line-wrapped)
        RemoteButton(
            onClick = { onKeyClick(LoeweKey.MENU) },
            shape = CircleShape,
            minSize = 54.dp,
            modifier = Modifier.size(54.dp)
        ) {
            Text(
                text = "MENU",
                color = LoeweTextSecondary,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                softWrap = false,
                textAlign = TextAlign.Center
            )
        }

        // Mute Button
        RemoteButton(
            onClick = { onKeyClick(LoeweKey.MUTE) },
            shape = CircleShape,
            minSize = 54.dp,
            modifier = Modifier.size(54.dp)
        ) {
            Text(
                text = "MUTE",
                color = LoeweTextSecondary,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                softWrap = false,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun PowerIcon(
    color: Color = LoewePowerRed,
    size: androidx.compose.ui.unit.Dp = 20.dp
) {
    Canvas(modifier = Modifier.size(size)) {
        val strokeWidth = 2.2.dp.toPx()
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

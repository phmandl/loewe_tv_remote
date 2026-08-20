package at.phman.loeweremote.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import at.phman.loeweremote.model.LoeweKey
import at.phman.loeweremote.ui.theme.AnthropicBorder
import at.phman.loeweremote.ui.theme.AnthropicParchment
import at.phman.loeweremote.ui.theme.AnthropicSand
import at.phman.loeweremote.ui.theme.AnthropicSurfaceElevated

@Composable
fun NumericPad(
    expanded: Boolean,
    onKeyClick: (LoeweKey) -> Unit,
    modifier: Modifier = Modifier,
    buttonHeight: Dp = 38.dp
) {
    AnimatedVisibility(
        visible = expanded,
        enter = expandVertically() + fadeIn(),
        exit = shrinkVertically() + fadeOut(),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 28.dp, vertical = 2.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            // Row 1: 1, 2, 3
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                NumpadButton(label = "1", height = buttonHeight, onClick = { onKeyClick(LoeweKey.NUM_1) }, modifier = Modifier.weight(1f))
                NumpadButton(label = "2", height = buttonHeight, onClick = { onKeyClick(LoeweKey.NUM_2) }, modifier = Modifier.weight(1f))
                NumpadButton(label = "3", height = buttonHeight, onClick = { onKeyClick(LoeweKey.NUM_3) }, modifier = Modifier.weight(1f))
            }

            // Row 2: 4, 5, 6
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                NumpadButton(label = "4", height = buttonHeight, onClick = { onKeyClick(LoeweKey.NUM_4) }, modifier = Modifier.weight(1f))
                NumpadButton(label = "5", height = buttonHeight, onClick = { onKeyClick(LoeweKey.NUM_5) }, modifier = Modifier.weight(1f))
                NumpadButton(label = "6", height = buttonHeight, onClick = { onKeyClick(LoeweKey.NUM_6) }, modifier = Modifier.weight(1f))
            }

            // Row 3: 7, 8, 9
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                NumpadButton(label = "7", height = buttonHeight, onClick = { onKeyClick(LoeweKey.NUM_7) }, modifier = Modifier.weight(1f))
                NumpadButton(label = "8", height = buttonHeight, onClick = { onKeyClick(LoeweKey.NUM_8) }, modifier = Modifier.weight(1f))
                NumpadButton(label = "9", height = buttonHeight, onClick = { onKeyClick(LoeweKey.NUM_9) }, modifier = Modifier.weight(1f))
            }

            // Row 4: TXT, 0, RAD
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                NumpadButton(label = "TXT", height = buttonHeight, textColor = AnthropicSand, onClick = { onKeyClick(LoeweKey.TEXT) }, modifier = Modifier.weight(1f))
                NumpadButton(label = "0", height = buttonHeight, onClick = { onKeyClick(LoeweKey.NUM_0) }, modifier = Modifier.weight(1f))
                NumpadButton(label = "RAD", height = buttonHeight, textColor = AnthropicSand, onClick = { onKeyClick(LoeweKey.RADIO) }, modifier = Modifier.weight(1f))
            }
        }
    }
}

@Composable
private fun NumpadButton(
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    height: Dp = 38.dp,
    textColor: Color = AnthropicParchment
) {
    RemoteButton(
        onClick = onClick,
        shape = RoundedCornerShape(10.dp),
        backgroundColor = AnthropicSurfaceElevated,
        borderColor = AnthropicBorder,
        minSize = height,
        modifier = modifier.height(height)
    ) {
        Text(
            text = label,
            color = textColor,
            fontSize = if (label.length > 1) 12.sp else 16.sp,
            fontFamily = FontFamily.Serif,
            fontWeight = FontWeight.SemiBold
        )
    }
}


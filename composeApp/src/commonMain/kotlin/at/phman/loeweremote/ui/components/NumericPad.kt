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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import at.phman.loeweremote.model.LoeweKey
import at.phman.loeweremote.ui.theme.LoeweTextPrimary
import at.phman.loeweremote.ui.theme.LoeweTextSecondary

@Composable
fun NumericPad(
    expanded: Boolean,
    onKeyClick: (LoeweKey) -> Unit,
    modifier: Modifier = Modifier
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
                .padding(horizontal = 32.dp, vertical = 6.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Row 1: 1, 2, 3
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                NumpadButton(label = "1", onClick = { onKeyClick(LoeweKey.NUM_1) }, modifier = Modifier.weight(1f))
                NumpadButton(label = "2", onClick = { onKeyClick(LoeweKey.NUM_2) }, modifier = Modifier.weight(1f))
                NumpadButton(label = "3", onClick = { onKeyClick(LoeweKey.NUM_3) }, modifier = Modifier.weight(1f))
            }

            // Row 2: 4, 5, 6
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                NumpadButton(label = "4", onClick = { onKeyClick(LoeweKey.NUM_4) }, modifier = Modifier.weight(1f))
                NumpadButton(label = "5", onClick = { onKeyClick(LoeweKey.NUM_5) }, modifier = Modifier.weight(1f))
                NumpadButton(label = "6", onClick = { onKeyClick(LoeweKey.NUM_6) }, modifier = Modifier.weight(1f))
            }

            // Row 3: 7, 8, 9
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                NumpadButton(label = "7", onClick = { onKeyClick(LoeweKey.NUM_7) }, modifier = Modifier.weight(1f))
                NumpadButton(label = "8", onClick = { onKeyClick(LoeweKey.NUM_8) }, modifier = Modifier.weight(1f))
                NumpadButton(label = "9", onClick = { onKeyClick(LoeweKey.NUM_9) }, modifier = Modifier.weight(1f))
            }

            // Row 4: TEXT, 0, RECORD
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                NumpadButton(label = "TXT", textColor = LoeweTextSecondary, onClick = { onKeyClick(LoeweKey.TEXT) }, modifier = Modifier.weight(1f))
                NumpadButton(label = "0", onClick = { onKeyClick(LoeweKey.NUM_0) }, modifier = Modifier.weight(1f))
                NumpadButton(label = "RAD", textColor = LoeweTextSecondary, onClick = { onKeyClick(LoeweKey.RADIO) }, modifier = Modifier.weight(1f))
            }
        }
    }
}

@Composable
private fun NumpadButton(
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    textColor: androidx.compose.ui.graphics.Color = LoeweTextPrimary
) {
    RemoteButton(
        onClick = onClick,
        shape = RoundedCornerShape(14.dp),
        modifier = modifier.height(46.dp)
    ) {
        Text(
            text = label,
            color = textColor,
            fontSize = 17.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

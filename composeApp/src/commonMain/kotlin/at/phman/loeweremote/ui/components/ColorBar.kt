package at.phman.loeweremote.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import at.phman.loeweremote.model.LoeweKey
import at.phman.loeweremote.ui.theme.GreekKeyBlue
import at.phman.loeweremote.ui.theme.GreekKeyGreen
import at.phman.loeweremote.ui.theme.GreekKeyRed
import at.phman.loeweremote.ui.theme.GreekKeyYellow

@Composable
fun ColorBar(
    onKeyClick: (LoeweKey) -> Unit,
    modifier: Modifier = Modifier,
    barHeight: Dp = 32.dp
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // RED (107 - Ancient Pompeian Red)
        RemoteButton(
            onClick = { onKeyClick(LoeweKey.RED) },
            backgroundColor = GreekKeyRed,
            borderColor = GreekKeyRed.copy(alpha = 0.85f),
            shape = RoundedCornerShape(10.dp),
            minSize = barHeight,
            modifier = Modifier
                .weight(1f)
                .height(barHeight)
        )

        // GREEN (108 - Olive Laurel Green)
        RemoteButton(
            onClick = { onKeyClick(LoeweKey.GREEN) },
            backgroundColor = GreekKeyGreen,
            borderColor = GreekKeyGreen.copy(alpha = 0.85f),
            shape = RoundedCornerShape(10.dp),
            minSize = barHeight,
            modifier = Modifier
                .weight(1f)
                .height(barHeight)
        )

        // YELLOW (109 - Athenian Gold Ochre)
        RemoteButton(
            onClick = { onKeyClick(LoeweKey.YELLOW) },
            backgroundColor = GreekKeyYellow,
            borderColor = GreekKeyYellow.copy(alpha = 0.85f),
            shape = RoundedCornerShape(10.dp),
            minSize = barHeight,
            modifier = Modifier
                .weight(1f)
                .height(barHeight)
        )

        // BLUE (110 - Aegean Lapis Lazuli)
        RemoteButton(
            onClick = { onKeyClick(LoeweKey.BLUE) },
            backgroundColor = GreekKeyBlue,
            borderColor = GreekKeyBlue.copy(alpha = 0.85f),
            shape = RoundedCornerShape(10.dp),
            minSize = barHeight,
            modifier = Modifier
                .weight(1f)
                .height(barHeight)
        )
    }
}


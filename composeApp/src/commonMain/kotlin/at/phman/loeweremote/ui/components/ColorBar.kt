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
import androidx.compose.ui.unit.dp
import at.phman.loeweremote.model.LoeweKey
import at.phman.loeweremote.ui.theme.LoeweKeyBlue
import at.phman.loeweremote.ui.theme.LoeweKeyGreen
import at.phman.loeweremote.ui.theme.LoeweKeyRed
import at.phman.loeweremote.ui.theme.LoeweKeyYellow

@Composable
fun ColorBar(
    onKeyClick: (LoeweKey) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // RED (107)
        RemoteButton(
            onClick = { onKeyClick(LoeweKey.RED) },
            backgroundColor = LoeweKeyRed,
            borderColor = LoeweKeyRed.copy(alpha = 0.8f),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .weight(1f)
                .height(38.dp)
        )

        // GREEN (108)
        RemoteButton(
            onClick = { onKeyClick(LoeweKey.GREEN) },
            backgroundColor = LoeweKeyGreen,
            borderColor = LoeweKeyGreen.copy(alpha = 0.8f),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .weight(1f)
                .height(38.dp)
        )

        // YELLOW (109)
        RemoteButton(
            onClick = { onKeyClick(LoeweKey.YELLOW) },
            backgroundColor = LoeweKeyYellow,
            borderColor = LoeweKeyYellow.copy(alpha = 0.8f),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .weight(1f)
                .height(38.dp)
        )

        // BLUE (110)
        RemoteButton(
            onClick = { onKeyClick(LoeweKey.BLUE) },
            backgroundColor = LoeweKeyBlue,
            borderColor = LoeweKeyBlue.copy(alpha = 0.8f),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .weight(1f)
                .height(38.dp)
        )
    }
}

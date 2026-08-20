package at.phman.loeweremote.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import at.phman.loeweremote.model.LoeweKey
import at.phman.loeweremote.ui.theme.LoeweAccent
import at.phman.loeweremote.ui.theme.LoeweBorder
import at.phman.loeweremote.ui.theme.LoeweSurfaceDark
import at.phman.loeweremote.ui.theme.LoeweSurfaceElevated
import at.phman.loeweremote.ui.theme.LoeweTextMuted
import at.phman.loeweremote.ui.theme.LoeweTextPrimary
import at.phman.loeweremote.ui.theme.LoeweTextSecondary

@Composable
fun RockerControl(
    onKeyClick: (LoeweKey) -> Unit,
    onToggleNumpad: () -> Unit,
    isNumpadExpanded: Boolean,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 28.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // VOLUME ROCKER
        VerticalRocker(
            label = "VOL",
            onPlusClick = { onKeyClick(LoeweKey.VOLUME_UP) },
            onMinusClick = { onKeyClick(LoeweKey.VOLUME_DOWN) }
        )

        // CENTER QUICK ACCESS (EPG & Numpad toggle)
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // EPG / GUIDE
            RemoteButton(
                onClick = { onKeyClick(LoeweKey.EPG) },
                shape = RoundedCornerShape(14.dp),
                minSize = 44.dp,
                modifier = Modifier.size(width = 68.dp, height = 44.dp)
            ) {
                Text(
                    text = "EPG",
                    color = LoeweTextSecondary,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            // 123 TOGGLE NUMPAD
            RemoteButton(
                onClick = onToggleNumpad,
                shape = RoundedCornerShape(14.dp),
                minSize = 44.dp,
                borderColor = if (isNumpadExpanded) LoeweAccent else LoeweBorder,
                backgroundColor = if (isNumpadExpanded) LoeweAccent.copy(alpha = 0.2f) else LoeweSurfaceElevated,
                modifier = Modifier.size(width = 68.dp, height = 44.dp)
            ) {
                Text(
                    text = "1 2 3",
                    color = if (isNumpadExpanded) LoeweAccent else LoeweTextPrimary,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        // PROGRAM / CHANNEL ROCKER
        VerticalRocker(
            label = "PROG",
            onPlusClick = { onKeyClick(LoeweKey.PROGRAM_UP) },
            onMinusClick = { onKeyClick(LoeweKey.PROGRAM_DOWN) }
        )
    }
}

@Composable
private fun VerticalRocker(
    label: String,
    onPlusClick: () -> Unit,
    onMinusClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .width(66.dp)
            .height(132.dp)
            .clip(RoundedCornerShape(32.dp))
            .background(LoeweSurfaceElevated)
            .border(1.2.dp, LoeweBorder, RoundedCornerShape(32.dp)),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // PLUS (+)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .clickable(onClick = onPlusClick),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "+",
                color = LoeweTextPrimary,
                fontSize = 24.sp,
                fontWeight = FontWeight.Light
            )
        }

        // CENTER LABEL
        Text(
            text = label,
            color = LoeweTextMuted,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp
        )

        // MINUS (-)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .clickable(onClick = onMinusClick),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "−",
                color = LoeweTextPrimary,
                fontSize = 24.sp,
                fontWeight = FontWeight.Light
            )
        }
    }
}

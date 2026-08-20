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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import at.phman.loeweremote.ui.theme.LoeweAccent
import at.phman.loeweremote.ui.theme.LoeweBorder
import at.phman.loeweremote.ui.theme.LoeweTextMuted
import at.phman.loeweremote.ui.theme.LoeweTextSecondary

@Composable
fun ConsoleLogBar(
    logs: List<String>,
    onClear: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    // Auto-scroll to latest log entry
    LaunchedEffect(logs.size) {
        if (logs.isNotEmpty()) {
            scrollState.animateScrollTo(scrollState.maxValue)
        }
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(Color(0xFF090A0E))
            .border(1.dp, Color(0xFF1E2333), RoundedCornerShape(10.dp))
            .padding(horizontal = 10.dp, vertical = 6.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            // Scrollable Console Text (Fixed ~3 lines height)
            Column(
                modifier = Modifier
                    .weight(1f)
                    .height(58.dp)
                    .verticalScroll(scrollState),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                if (logs.isEmpty()) {
                    Text(
                        text = "Console ready. No logs.",
                        color = LoeweTextMuted,
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace
                    )
                } else {
                    logs.forEach { entry ->
                        val color = when {
                            entry.startsWith("Error", ignoreCase = true) || entry.contains("failed", ignoreCase = true) -> Color(0xFFFF5252)
                            entry.startsWith("OK", ignoreCase = true) || entry.contains("Connected", ignoreCase = true) -> Color(0xFF4CAF50)
                            entry.startsWith("Sending", ignoreCase = true) -> LoeweAccent
                            else -> LoeweTextSecondary
                        }

                        Text(
                            text = "> $entry",
                            color = color,
                            fontSize = 11.sp,
                            lineHeight = 14.sp,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }
            }

            // Clear Log Button
            if (logs.isNotEmpty()) {
                Text(
                    text = "CLR",
                    color = LoeweTextMuted,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace,
                    modifier = Modifier
                        .padding(start = 6.dp)
                        .clickable(onClick = onClear)
                )
            }
        }
    }
}

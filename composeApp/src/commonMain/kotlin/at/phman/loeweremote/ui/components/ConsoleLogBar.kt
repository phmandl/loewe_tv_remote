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
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import at.phman.loeweremote.ui.theme.AnthropicBorder
import at.phman.loeweremote.ui.theme.AnthropicMuted
import at.phman.loeweremote.ui.theme.AnthropicSand
import at.phman.loeweremote.ui.theme.AnthropicTerracotta
import at.phman.loeweremote.ui.theme.GreekStatusConnected
import at.phman.loeweremote.ui.theme.GreekStatusError

@Composable
fun ConsoleLogBar(
    logs: List<String>,
    onClear: () -> Unit,
    modifier: Modifier = Modifier,
    height: Dp = 42.dp
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
            .padding(horizontal = 16.dp, vertical = 2.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(Color(0xFF0E0D0C))
            .border(1.dp, AnthropicBorder, RoundedCornerShape(8.dp))
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            // Scrollable Console Text
            Column(
                modifier = Modifier
                    .weight(1f)
                    .height(height)
                    .verticalScroll(scrollState),
                verticalArrangement = Arrangement.spacedBy(1.dp)
            ) {
                if (logs.isEmpty()) {
                    Text(
                        text = "Hermes ready. Listening...",
                        color = AnthropicMuted,
                        fontSize = 10.sp,
                        fontFamily = FontFamily.Monospace
                    )
                } else {
                    logs.forEach { entry ->
                        val color = when {
                            entry.startsWith("Error", ignoreCase = true) || entry.contains("failed", ignoreCase = true) -> GreekStatusError
                            entry.startsWith("OK", ignoreCase = true) || entry.contains("Connected", ignoreCase = true) -> GreekStatusConnected
                            entry.startsWith("Sending", ignoreCase = true) -> AnthropicTerracotta
                            else -> AnthropicSand
                        }

                        Text(
                            text = "> $entry",
                            color = color,
                            fontSize = 10.sp,
                            lineHeight = 13.sp,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }
            }

            // Clear Log Button
            if (logs.isNotEmpty()) {
                Text(
                    text = "CLR",
                    color = AnthropicMuted,
                    fontSize = 9.sp,
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


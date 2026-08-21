package at.phman.loeweremote.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import at.phman.loeweremote.model.ConnectionState
import at.phman.loeweremote.ui.theme.AnthropicBorder
import at.phman.loeweremote.ui.theme.AnthropicGoldBorder
import at.phman.loeweremote.ui.theme.AnthropicParchment
import at.phman.loeweremote.ui.theme.AnthropicSand
import at.phman.loeweremote.ui.theme.AnthropicSurfaceElevated
import at.phman.loeweremote.ui.theme.AnthropicTerracotta
import at.phman.loeweremote.ui.theme.AnthropicTerracottaBg
import at.phman.loeweremote.ui.theme.GreekStatusConnected
import at.phman.loeweremote.ui.theme.GreekStatusConnecting
import at.phman.loeweremote.ui.theme.GreekStatusDisconnected
import at.phman.loeweremote.ui.theme.GreekStatusError

@Composable
fun HeaderBar(
    connectionState: ConnectionState,
    tvChassis: String? = null,
    tvPowerState: String? = null,
    isWolSending: Boolean,
    isSendingCommand: Boolean,
    onStatusClick: () -> Unit,
    onWolClick: () -> Unit,
    onSettingsClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 14.dp, vertical = 4.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Connection Status Pill with auto-detected chassis & live power mode
            val (statusColor, statusText) = when (connectionState) {
                is ConnectionState.Connected -> {
                    val deviceLabel = if (!tvChassis.isNullOrBlank()) "Loewe $tvChassis" else "Loewe bild"
                    if (tvPowerState?.equals("idle", ignoreCase = true) == true) {
                        Pair(GreekStatusConnecting, "$deviceLabel (Standby)")
                    } else {
                        Pair(GreekStatusConnected, "$deviceLabel Online")
                    }
                }
                is ConnectionState.Connecting -> Pair(GreekStatusConnecting, "Connecting...")
                is ConnectionState.Disconnected -> Pair(GreekStatusDisconnected, "Offline")
                is ConnectionState.Error -> Pair(GreekStatusError, "Error (Retry)")
            }

            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(AnthropicSurfaceElevated)
                    .border(1.2.dp, AnthropicBorder, RoundedCornerShape(20.dp))
                    .clickable(onClick = onStatusClick)
                    .padding(horizontal = 14.dp, vertical = 9.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(8.5.dp)
                        .clip(CircleShape)
                        .background(statusColor)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = statusText,
                    color = AnthropicParchment,
                    fontSize = 13.sp,
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.Medium
                )
                if (isSendingCommand) {
                    Spacer(modifier = Modifier.width(7.dp))
                    CircularProgressIndicator(
                        modifier = Modifier.size(11.dp),
                        strokeWidth = 1.6.dp,
                        color = AnthropicTerracotta
                    )
                }
            }

            // Action buttons (WoL & Settings)
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Wake-on-LAN Button
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(14.dp))
                        .background(if (isWolSending) AnthropicTerracottaBg else AnthropicSurfaceElevated)
                        .border(1.2.dp, if (isWolSending) AnthropicTerracotta else AnthropicBorder, RoundedCornerShape(14.dp))
                        .clickable(enabled = !isWolSending, onClick = onWolClick)
                        .padding(horizontal = 14.dp, vertical = 9.dp),
                    contentAlignment = Alignment.Center
                ) {
                    if (isWolSending) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(14.dp),
                            strokeWidth = 2.dp,
                            color = AnthropicTerracotta
                        )
                    } else {
                        Text(
                            text = "⚡ WoL",
                            color = AnthropicTerracotta,
                            fontSize = 13.sp,
                            fontFamily = FontFamily.Serif,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // Settings Button
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(14.dp))
                        .background(AnthropicSurfaceElevated)
                        .border(1.2.dp, AnthropicBorder, RoundedCornerShape(14.dp))
                        .clickable(onClick = onSettingsClick)
                        .padding(horizontal = 14.dp, vertical = 9.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "⚙ Settings",
                        color = AnthropicSand,
                        fontSize = 13.sp,
                        fontFamily = FontFamily.Serif,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

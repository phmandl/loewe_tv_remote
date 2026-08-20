package at.phman.loeweremote.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import at.phman.loeweremote.model.ConnectionState
import at.phman.loeweremote.ui.theme.LoeweAccent
import at.phman.loeweremote.ui.theme.LoeweBorder
import at.phman.loeweremote.ui.theme.LoeweStatusConnected
import at.phman.loeweremote.ui.theme.LoeweStatusConnecting
import at.phman.loeweremote.ui.theme.LoeweStatusDisconnected
import at.phman.loeweremote.ui.theme.LoeweStatusError
import at.phman.loeweremote.ui.theme.LoeweSurfaceDark
import at.phman.loeweremote.ui.theme.LoeweSurfaceElevated
import at.phman.loeweremote.ui.theme.LoeweTextMuted
import at.phman.loeweremote.ui.theme.LoeweTextPrimary
import at.phman.loeweremote.ui.theme.LoeweTextSecondary

@Composable
fun HeaderBar(
    connectionState: ConnectionState,
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
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Connection Status Pill
            val (statusColor, statusText) = when (connectionState) {
                is ConnectionState.Connected -> Pair(LoeweStatusConnected, "bild 5 Online")
                is ConnectionState.Connecting -> Pair(LoeweStatusConnecting, "Connecting...")
                is ConnectionState.Disconnected -> Pair(LoeweStatusDisconnected, "Offline")
                is ConnectionState.Error -> Pair(LoeweStatusError, "Error (Tap to retry)")
            }

            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(LoeweSurfaceElevated)
                    .border(1.dp, LoeweBorder, RoundedCornerShape(20.dp))
                    .clickable(onClick = onStatusClick)
                    .padding(horizontal = 12.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(statusColor)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = statusText,
                    color = LoeweTextPrimary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )
                if (isSendingCommand) {
                    Spacer(modifier = Modifier.width(6.dp))
                    CircularProgressIndicator(
                        modifier = Modifier.size(10.dp),
                        strokeWidth = 1.5.dp,
                        color = LoeweAccent
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
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (isWolSending) LoeweAccent.copy(alpha = 0.2f) else LoeweSurfaceElevated)
                        .border(1.dp, LoeweBorder, RoundedCornerShape(12.dp))
                        .clickable(enabled = !isWolSending, onClick = onWolClick)
                        .padding(horizontal = 10.dp, vertical = 6.dp),
                    contentAlignment = Alignment.Center
                ) {
                    if (isWolSending) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(14.dp),
                            strokeWidth = 2.dp,
                            color = LoeweAccent
                        )
                    } else {
                        Text(
                            text = "⚡ WoL",
                            color = LoeweAccent,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // Settings Button
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(LoeweSurfaceElevated)
                        .border(1.dp, LoeweBorder, RoundedCornerShape(12.dp))
                        .clickable(onClick = onSettingsClick)
                        .padding(horizontal = 10.dp, vertical = 6.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "⚙ Settings",
                        color = LoeweTextSecondary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

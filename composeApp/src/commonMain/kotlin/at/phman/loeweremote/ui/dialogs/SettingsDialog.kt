package at.phman.loeweremote.ui.dialogs

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import at.phman.loeweremote.model.RemoteSettingsState
import at.phman.loeweremote.ui.components.RemoteButton
import at.phman.loeweremote.ui.theme.AnthropicBorder
import at.phman.loeweremote.ui.theme.AnthropicGoldBorder
import at.phman.loeweremote.ui.theme.AnthropicMuted
import at.phman.loeweremote.ui.theme.AnthropicParchment
import at.phman.loeweremote.ui.theme.AnthropicSand
import at.phman.loeweremote.ui.theme.AnthropicSurfaceDark
import at.phman.loeweremote.ui.theme.AnthropicSurfaceElevated
import at.phman.loeweremote.ui.theme.AnthropicTerracotta

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsDialog(
    initialSettings: RemoteSettingsState,
    onSave: (RemoteSettingsState) -> Unit,
    onDismiss: () -> Unit
) {
    var ipAddress by remember { mutableStateOf(initialSettings.ipAddress) }
    var macAddress by remember { mutableStateOf(initialSettings.macAddress) }
    var portText by remember { mutableStateOf(initialSettings.port.toString()) }
    var deviceName by remember { mutableStateOf(initialSettings.deviceName) }
    var showConsoleLog by remember { mutableStateOf(initialSettings.showConsoleLog) }

    BasicAlertDialog(
        onDismissRequest = onDismiss
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .background(AnthropicSurfaceDark)
                .border(1.2.dp, AnthropicGoldBorder.copy(alpha = 0.6f), RoundedCornerShape(20.dp))
                .padding(20.dp)
                .imePadding()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Loewe Configuration",
                    color = AnthropicParchment,
                    fontSize = 18.sp,
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.Bold
                )
            }

            Text(
                text = "Target: Loewe bild TV (Chassis SL410 / SL420 / SL3xx / SL5xx)",
                color = AnthropicMuted,
                fontSize = 11.5.sp,
                fontFamily = FontFamily.Serif
            )

            // IP Address Field
            OutlinedTextField(
                value = ipAddress,
                onValueChange = { ipAddress = it },
                label = { Text("TV IP Address", fontFamily = FontFamily.Serif) },
                placeholder = { Text("e.g. 192.168.1.100") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                colors = textFieldColors(),
                modifier = Modifier.fillMaxWidth()
            )

            // MAC Address Field (for WoL)
            OutlinedTextField(
                value = macAddress,
                onValueChange = { macAddress = it },
                label = { Text("TV MAC Address (for WoL)", fontFamily = FontFamily.Serif) },
                placeholder = { Text("e.g. 00:11:22:33:44:55") },
                singleLine = true,
                colors = textFieldColors(),
                modifier = Modifier.fillMaxWidth()
            )

            // Wake-on-LAN Information Card
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(AnthropicSurfaceElevated)
                    .border(1.dp, AnthropicBorder, RoundedCornerShape(10.dp))
                    .padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = "⚡ Wake on LAN on Loewe TV",
                    color = AnthropicTerracotta,
                    fontSize = 12.sp,
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "To enable WoL on your TV:\n1. System settings → Multimedia / Network\n2. Network configuration → Wake on LAN (On/Active)\n3. (Optional) Enable 'Quick Startup Mode' or 'Mobile App Standby'",
                    color = AnthropicSand,
                    fontSize = 11.sp,
                    fontFamily = FontFamily.Serif,
                    lineHeight = 15.sp
                )
            }

            // Port Field
            OutlinedTextField(
                value = portText,
                onValueChange = { portText = it },
                label = { Text("SOAP Port (Default 905)", fontFamily = FontFamily.Serif) },
                placeholder = { Text("905") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                colors = textFieldColors(),
                modifier = Modifier.fillMaxWidth()
            )

            // Device Name
            OutlinedTextField(
                value = deviceName,
                onValueChange = { deviceName = it },
                label = { Text("Client Device Name", fontFamily = FontFamily.Serif) },
                placeholder = { Text("Loewe Remote App") },
                singleLine = true,
                colors = textFieldColors(),
                modifier = Modifier.fillMaxWidth()
            )

            // Debug Console Log Toggle
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(AnthropicSurfaceElevated)
                    .border(1.dp, AnthropicBorder, RoundedCornerShape(10.dp))
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f).padding(end = 8.dp)) {
                    Text(
                        text = "Debug Log Console",
                        color = AnthropicParchment,
                        fontSize = 12.5.sp,
                        fontFamily = FontFamily.Serif,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "Display live SOAP / WoL communication bar",
                        color = AnthropicMuted,
                        fontSize = 10.5.sp,
                        fontFamily = FontFamily.Serif
                    )
                }
                androidx.compose.material3.Switch(
                    checked = showConsoleLog,
                    onCheckedChange = { showConsoleLog = it },
                    colors = androidx.compose.material3.SwitchDefaults.colors(
                        checkedThumbColor = AnthropicParchment,
                        checkedTrackColor = AnthropicTerracotta,
                        uncheckedThumbColor = AnthropicSand,
                        uncheckedTrackColor = AnthropicSurfaceDark,
                        uncheckedBorderColor = AnthropicBorder
                    )
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Cancel
                RemoteButton(
                    onClick = onDismiss,
                    shape = RoundedCornerShape(10.dp),
                    backgroundColor = AnthropicSurfaceElevated,
                    borderColor = AnthropicBorder,
                    modifier = Modifier
                        .weight(1f)
                        .height(44.dp)
                ) {
                    Text("Cancel", color = AnthropicSand, fontSize = 13.sp, fontFamily = FontFamily.Serif)
                }

                // Save
                RemoteButton(
                    onClick = {
                        val parsedPort = portText.trim().toIntOrNull() ?: 905
                        val updated = initialSettings.copy(
                            ipAddress = ipAddress.trim(),
                            macAddress = macAddress.trim(),
                            port = parsedPort,
                            deviceName = deviceName.trim(),
                            showConsoleLog = showConsoleLog
                        )
                        onSave(updated)
                    },
                    shape = RoundedCornerShape(10.dp),
                    backgroundColor = AnthropicTerracotta,
                    borderColor = AnthropicTerracotta,
                    modifier = Modifier
                        .weight(1f)
                        .height(44.dp)
                ) {
                    Text("Save & Connect", color = AnthropicParchment, fontSize = 13.sp, fontFamily = FontFamily.Serif, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
private fun textFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor = AnthropicTerracotta,
    unfocusedBorderColor = AnthropicBorder,
    focusedLabelColor = AnthropicTerracotta,
    unfocusedLabelColor = AnthropicSand,
    focusedTextColor = AnthropicParchment,
    unfocusedTextColor = AnthropicParchment,
    cursorColor = AnthropicTerracotta
)


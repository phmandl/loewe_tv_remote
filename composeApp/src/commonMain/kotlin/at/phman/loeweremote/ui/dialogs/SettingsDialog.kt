package at.phman.loeweremote.ui.dialogs

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import at.phman.loeweremote.model.RemoteSettingsState
import at.phman.loeweremote.ui.components.RemoteButton
import at.phman.loeweremote.ui.theme.LoeweAccent
import at.phman.loeweremote.ui.theme.LoeweBorder
import at.phman.loeweremote.ui.theme.LoeweSurfaceDark
import at.phman.loeweremote.ui.theme.LoeweSurfaceElevated
import at.phman.loeweremote.ui.theme.LoeweTextMuted
import at.phman.loeweremote.ui.theme.LoeweTextPrimary
import at.phman.loeweremote.ui.theme.LoeweTextSecondary

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

    BasicAlertDialog(
        onDismissRequest = onDismiss
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(24.dp))
                .background(LoeweSurfaceDark)
                .border(1.dp, LoeweBorder, RoundedCornerShape(24.dp))
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Loewe TV Configuration",
                    color = LoeweTextPrimary,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Text(
                text = "Target: Loewe bild 5 (Chassis SL420, Loewe OS)",
                color = LoeweTextMuted,
                fontSize = 12.sp
            )

            // IP Address Field
            OutlinedTextField(
                value = ipAddress,
                onValueChange = { ipAddress = it },
                label = { Text("TV IP Address") },
                placeholder = { Text("e.g. 192.168.1.100") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                colors = textFieldColors(),
                modifier = Modifier.fillMaxWidth()
            )

            // MAC Address Field (for WoL)
            OutlinedTextField(
                value = macAddress,
                onValueChange = { macAddress = it },
                label = { Text("TV MAC Address (for WoL)") },
                placeholder = { Text("e.g. 00:11:22:33:44:55") },
                singleLine = true,
                colors = textFieldColors(),
                modifier = Modifier.fillMaxWidth()
            )

            // Port Field
            OutlinedTextField(
                value = portText,
                onValueChange = { portText = it },
                label = { Text("SOAP Port (Default 905)") },
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
                label = { Text("Client Device Name") },
                placeholder = { Text("Loewe Remote App") },
                singleLine = true,
                colors = textFieldColors(),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Cancel
                RemoteButton(
                    onClick = onDismiss,
                    shape = RoundedCornerShape(12.dp),
                    backgroundColor = LoeweSurfaceElevated,
                    modifier = Modifier
                        .weight(1f)
                        .height(46.dp)
                ) {
                    Text("Cancel", color = LoeweTextSecondary, fontSize = 14.sp)
                }

                // Save
                RemoteButton(
                    onClick = {
                        val parsedPort = portText.trim().toIntOrNull() ?: 905
                        val updated = initialSettings.copy(
                            ipAddress = ipAddress.trim(),
                            macAddress = macAddress.trim(),
                            port = parsedPort,
                            deviceName = deviceName.trim()
                        )
                        onSave(updated)
                    },
                    shape = RoundedCornerShape(12.dp),
                    backgroundColor = LoeweAccent,
                    borderColor = LoeweAccent,
                    modifier = Modifier
                        .weight(1f)
                        .height(46.dp)
                ) {
                    Text("Save & Connect", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
private fun textFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor = LoeweAccent,
    unfocusedBorderColor = LoeweBorder,
    focusedLabelColor = LoeweAccent,
    unfocusedLabelColor = LoeweTextSecondary,
    focusedTextColor = LoeweTextPrimary,
    unfocusedTextColor = LoeweTextPrimary,
    cursorColor = LoeweAccent
)

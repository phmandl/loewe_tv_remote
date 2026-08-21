package at.phman.loeweremote.ui.dialogs

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.TextStyle
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
import at.phman.loeweremote.ui.theme.GreekStatusConnected
import at.phman.loeweremote.ui.theme.GreekStatusConnecting

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsDialog(
    initialSettings: RemoteSettingsState,
    tvChassis: String? = null,
    tvSoftwareVersion: String? = null,
    tvWolEnable: Boolean? = null,
    tvWolInteractive: Boolean? = null,
    onSaveTvWolSettings: ((wolEnable: Boolean, wolInteractive: Boolean) -> Unit)? = null,
    onSave: (RemoteSettingsState) -> Unit,
    onDismiss: () -> Unit
) {
    var ipAddress by remember { mutableStateOf(initialSettings.ipAddress) }
    var autoMacDiscovery by remember { mutableStateOf(initialSettings.autoMacDiscovery) }
    var manualMacAddress by remember {
        mutableStateOf(
            initialSettings.manualMacAddress.ifBlank {
                if (!initialSettings.autoMacDiscovery) initialSettings.macAddress else ""
            }
        )
    }
    var autoMacAddress by remember {
        mutableStateOf(
            initialSettings.autoMacAddress.ifBlank {
                if (initialSettings.autoMacDiscovery) initialSettings.macAddress else ""
            }
        )
    }
    var portText by remember { mutableStateOf(initialSettings.port.toString()) }
    var deviceName by remember { mutableStateOf(initialSettings.deviceName) }
    var showConsoleLog by remember { mutableStateOf(initialSettings.showConsoleLog) }

    // Collapsible TV WoL settings & diagnostics
    var isWolSettingsExpanded by remember { mutableStateOf(false) }
    var wolEnableSwitch by remember { mutableStateOf(tvWolEnable ?: true) }
    var wolInteractiveSwitch by remember { mutableStateOf(tvWolInteractive ?: true) }

    BasicAlertDialog(
        onDismissRequest = onDismiss
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(18.dp))
                .background(AnthropicSurfaceDark)
                .border(1.2.dp, AnthropicGoldBorder.copy(alpha = 0.6f), RoundedCornerShape(18.dp))
                .padding(horizontal = 16.dp, vertical = 14.dp)
                .imePadding()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Header & Status Badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Loewe Configuration",
                    color = AnthropicParchment,
                    fontSize = 16.5.sp,
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.Bold
                )

                if (!tvChassis.isNullOrBlank()) {
                    Text(
                        text = "Loewe $tvChassis",
                        color = AnthropicTerracotta,
                        fontSize = 11.5.sp,
                        fontFamily = FontFamily.Serif,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            // TV IP Address Field
            OutlinedTextField(
                value = ipAddress,
                onValueChange = { ipAddress = it },
                label = { Text("TV IP Address", fontSize = 11.5.sp, fontFamily = FontFamily.Serif) },
                placeholder = { Text("e.g. 192.168.1.100", fontSize = 12.sp) },
                singleLine = true,
                textStyle = TextStyle(fontSize = 13.sp, fontFamily = FontFamily.Serif),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                colors = textFieldColors(),
                modifier = Modifier.fillMaxWidth()
            )

            // Auto MAC Discovery Toggle
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(AnthropicSurfaceElevated)
                    .border(1.dp, AnthropicBorder, RoundedCornerShape(8.dp))
                    .padding(horizontal = 10.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f).padding(end = 6.dp)) {
                    Text(
                        text = "Auto MAC Discovery",
                        color = AnthropicParchment,
                        fontSize = 11.5.sp,
                        fontFamily = FontFamily.Serif,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = if (autoMacDiscovery) "Auto-fetches MAC over SOAP" else "Manual override for WoL MAC",
                        color = AnthropicMuted,
                        fontSize = 9.5.sp,
                        fontFamily = FontFamily.Serif
                    )
                }
                Switch(
                    checked = autoMacDiscovery,
                    onCheckedChange = { autoMacDiscovery = it },
                    colors = switchColors()
                )
            }

            // MAC Address Field (Greyed out & auto-filled when Auto Discovery is enabled; editable when disabled)
            val displayedMac = if (autoMacDiscovery) {
                autoMacAddress.ifBlank { "Auto-detecting on connect..." }
            } else {
                manualMacAddress
            }

            OutlinedTextField(
                value = displayedMac,
                onValueChange = {
                    if (!autoMacDiscovery) {
                        manualMacAddress = it
                    }
                },
                enabled = !autoMacDiscovery,
                label = {
                    Text(
                        if (autoMacDiscovery) "TV MAC (Auto-Discovered)" else "TV MAC (Manual for WoL)",
                        fontSize = 11.5.sp,
                        fontFamily = FontFamily.Serif
                    )
                },
                placeholder = { Text("e.g. 00:11:22:33:44:55", fontSize = 12.sp) },
                singleLine = true,
                textStyle = TextStyle(fontSize = 13.sp, fontFamily = FontFamily.Serif),
                colors = textFieldColors(),
                modifier = Modifier.fillMaxWidth()
            )

            // Ausklappbare TV Wake-on-LAN Settings & Diagnostics
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(AnthropicSurfaceElevated)
                    .border(1.dp, AnthropicBorder, RoundedCornerShape(8.dp))
                    .clickable { isWolSettingsExpanded = !isWolSettingsExpanded }
                    .padding(horizontal = 10.dp, vertical = 7.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "⚡ TV Wake-on-LAN Settings",
                        color = AnthropicParchment,
                        fontSize = 11.5.sp,
                        fontFamily = FontFamily.Serif,
                        fontWeight = FontWeight.SemiBold
                    )

                    val (badgeColor, badgeText) = when {
                        tvWolEnable == false -> Pair(AnthropicTerracotta, "● Disabled")
                        tvWolEnable == true && tvWolInteractive == true -> Pair(GreekStatusConnected, "● Active & Direct")
                        tvWolEnable == true && tvWolInteractive == false -> Pair(GreekStatusConnecting, "● Standby Only")
                        else -> Pair(AnthropicSand.copy(alpha = 0.8f), if (isWolSettingsExpanded) "▲ Close" else "▼ Configure")
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = badgeText,
                            color = badgeColor,
                            fontSize = 10.sp,
                            fontFamily = FontFamily.Serif,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = if (isWolSettingsExpanded) "▲" else "▼",
                            color = AnthropicSand.copy(alpha = 0.7f),
                            fontSize = 9.sp
                        )
                    }
                }

                AnimatedVisibility(
                    visible = isWolSettingsExpanded,
                    enter = fadeIn() + expandVertically(),
                    exit = fadeOut() + shrinkVertically()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        // Switch 1: WolEnable
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(6.dp))
                                .background(AnthropicSurfaceDark.copy(alpha = 0.5f))
                                .padding(horizontal = 8.dp, vertical = 5.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f).padding(end = 6.dp)) {
                                Text(
                                    text = "Wake on LAN (WolEnable)",
                                    color = AnthropicParchment,
                                    fontSize = 11.sp,
                                    fontFamily = FontFamily.Serif,
                                    fontWeight = FontWeight.Medium
                                )
                                Text(
                                    text = "Keep TV network chip active in sleep mode",
                                    color = AnthropicMuted,
                                    fontSize = 9.sp,
                                    fontFamily = FontFamily.Serif
                                )
                            }
                            Switch(
                                checked = wolEnableSwitch,
                                onCheckedChange = {
                                    wolEnableSwitch = it
                                    onSaveTvWolSettings?.invoke(it, wolInteractiveSwitch)
                                },
                                colors = switchColors()
                            )
                        }

                        // Switch 2: WolInteractive
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(6.dp))
                                .background(AnthropicSurfaceDark.copy(alpha = 0.5f))
                                .padding(horizontal = 8.dp, vertical = 5.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f).padding(end = 6.dp)) {
                                Text(
                                    text = "Direct Screen On (WolInteractive)",
                                    color = if (wolEnableSwitch) AnthropicParchment else AnthropicMuted,
                                    fontSize = 11.sp,
                                    fontFamily = FontFamily.Serif,
                                    fontWeight = FontWeight.Medium
                                )
                                Text(
                                    text = if (wolInteractiveSwitch) "Wakes directly with screen on" else "Wakes to silent network standby",
                                    color = AnthropicMuted,
                                    fontSize = 9.sp,
                                    fontFamily = FontFamily.Serif
                                )
                            }
                            Switch(
                                checked = wolInteractiveSwitch,
                                enabled = wolEnableSwitch,
                                onCheckedChange = {
                                    wolInteractiveSwitch = it
                                    onSaveTvWolSettings?.invoke(wolEnableSwitch, it)
                                },
                                colors = switchColors()
                            )
                        }

                        // Setup Hint
                        Text(
                            text = "ℹ️ Syncs live with TV System settings → Multimedia / Network → Wake on LAN",
                            color = AnthropicSand.copy(alpha = 0.8f),
                            fontSize = 9.sp,
                            fontFamily = FontFamily.Serif,
                            lineHeight = 12.sp,
                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                        )
                    }
                }
            }

            // Compact Side-by-Side: Port + Device Name
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = portText,
                    onValueChange = { portText = it },
                    label = { Text("Port", fontSize = 11.5.sp, fontFamily = FontFamily.Serif) },
                    placeholder = { Text("905", fontSize = 12.sp) },
                    singleLine = true,
                    textStyle = TextStyle(fontSize = 13.sp, fontFamily = FontFamily.Serif),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    colors = textFieldColors(),
                    modifier = Modifier.weight(0.33f)
                )

                OutlinedTextField(
                    value = deviceName,
                    onValueChange = { deviceName = it },
                    label = { Text("Client Name", fontSize = 11.5.sp, fontFamily = FontFamily.Serif) },
                    placeholder = { Text("Loewe Remote", fontSize = 12.sp) },
                    singleLine = true,
                    textStyle = TextStyle(fontSize = 13.sp, fontFamily = FontFamily.Serif),
                    colors = textFieldColors(),
                    modifier = Modifier.weight(0.67f)
                )
            }

            // Debug Console Log Toggle
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(AnthropicSurfaceElevated)
                    .border(1.dp, AnthropicBorder, RoundedCornerShape(8.dp))
                    .padding(horizontal = 10.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f).padding(end = 6.dp)) {
                    Text(
                        text = "Debug Log Console",
                        color = AnthropicParchment,
                        fontSize = 11.5.sp,
                        fontFamily = FontFamily.Serif,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "Live SOAP & WoL communication terminal",
                        color = AnthropicMuted,
                        fontSize = 9.5.sp,
                        fontFamily = FontFamily.Serif
                    )
                }
                Switch(
                    checked = showConsoleLog,
                    onCheckedChange = { showConsoleLog = it },
                    colors = switchColors()
                )
            }

            Spacer(modifier = Modifier.height(2.dp))

            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Cancel
                RemoteButton(
                    onClick = onDismiss,
                    shape = RoundedCornerShape(8.dp),
                    backgroundColor = AnthropicSurfaceElevated,
                    borderColor = AnthropicBorder,
                    modifier = Modifier
                        .weight(1f)
                        .height(38.dp)
                ) {
                    Text("Cancel", color = AnthropicSand, fontSize = 12.5.sp, fontFamily = FontFamily.Serif)
                }

                // Save
                RemoteButton(
                    onClick = {
                        val parsedPort = portText.trim().toIntOrNull() ?: 905
                        val effectiveMac = if (autoMacDiscovery) autoMacAddress else manualMacAddress
                        val updated = initialSettings.copy(
                            ipAddress = ipAddress.trim(),
                            macAddress = effectiveMac.trim(),
                            manualMacAddress = manualMacAddress.trim(),
                            autoMacAddress = autoMacAddress.trim(),
                            autoMacDiscovery = autoMacDiscovery,
                            port = parsedPort,
                            deviceName = deviceName.trim(),
                            showConsoleLog = showConsoleLog
                        )
                        // Also persist any updated TV WoL settings to TV
                        onSaveTvWolSettings?.invoke(wolEnableSwitch, wolInteractiveSwitch)
                        onSave(updated)
                    },
                    shape = RoundedCornerShape(8.dp),
                    backgroundColor = AnthropicTerracotta,
                    borderColor = AnthropicTerracotta,
                    modifier = Modifier
                        .weight(1f)
                        .height(38.dp)
                ) {
                    Text(
                        "Save & Connect",
                        color = AnthropicParchment,
                        fontSize = 12.5.sp,
                        fontFamily = FontFamily.Serif,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
private fun switchColors() = SwitchDefaults.colors(
    checkedThumbColor = AnthropicParchment,
    checkedTrackColor = AnthropicTerracotta,
    uncheckedThumbColor = AnthropicSand,
    uncheckedTrackColor = AnthropicSurfaceDark,
    uncheckedBorderColor = AnthropicBorder
)

@Composable
private fun textFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor = AnthropicTerracotta,
    unfocusedBorderColor = AnthropicBorder,
    focusedLabelColor = AnthropicTerracotta,
    unfocusedLabelColor = AnthropicSand,
    focusedTextColor = AnthropicParchment,
    unfocusedTextColor = AnthropicParchment,
    disabledTextColor = AnthropicParchment.copy(alpha = 0.55f),
    disabledBorderColor = AnthropicBorder.copy(alpha = 0.4f),
    disabledLabelColor = AnthropicMuted,
    disabledPlaceholderColor = AnthropicMuted.copy(alpha = 0.45f),
    disabledContainerColor = AnthropicSurfaceDark.copy(alpha = 0.35f),
    cursorColor = AnthropicTerracotta
)

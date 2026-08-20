package at.phman.loeweremote

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.remember
import at.phman.loeweremote.ui.components.ColorBar
import at.phman.loeweremote.ui.components.DPadControl
import at.phman.loeweremote.ui.components.HeaderBar
import at.phman.loeweremote.ui.components.NumericPad
import at.phman.loeweremote.ui.components.RockerControl
import at.phman.loeweremote.ui.components.TopControls
import at.phman.loeweremote.ui.dialogs.SettingsDialog
import at.phman.loeweremote.ui.theme.LoeweBgDark
import at.phman.loeweremote.ui.theme.LoeweRemoteTheme
import at.phman.loeweremote.viewmodel.RemoteViewModel

@Composable
fun App(
    viewModel: RemoteViewModel = remember { RemoteViewModel() }
) {
    val uiState by viewModel.uiState.collectAsState()
    val settingsState by viewModel.settingsState.collectAsState()

    LoeweRemoteTheme {
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .background(LoeweBgDark),
            color = LoeweBgDark
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .statusBarsPadding()
                    .navigationBarsPadding()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState()),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // 1. Header Bar: Connection Status + WoL + Settings
                    HeaderBar(
                        connectionState = uiState.connectionState,
                        isWolSending = uiState.isWolSending,
                        isSendingCommand = uiState.isSendingCommand,
                        onStatusClick = { viewModel.connect() },
                        onWolClick = { viewModel.sendWakeOnLan() },
                        onSettingsClick = { viewModel.toggleSettings(true) }
                    )

                    // 1b. Live Debug Terminal (Always visible, 3 lines, scrollable)
                    at.phman.loeweremote.ui.components.ConsoleLogBar(
                        logs = uiState.logs,
                        onClear = { viewModel.clearLogs() }
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    // 2. Top Controls: Power, Info, Menu, Mute
                    TopControls(
                        onKeyClick = { key -> viewModel.sendKey(key) }
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    // 3. Navigation D-Pad & Back / Home
                    DPadControl(
                        onKeyClick = { key -> viewModel.sendKey(key) }
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // 4. Volume & Channel Rockers + EPG / Numpad toggle
                    RockerControl(
                        onKeyClick = { key -> viewModel.sendKey(key) },
                        onToggleNumpad = { viewModel.toggleNumpad() },
                        isNumpadExpanded = uiState.isNumpadExpanded
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    // 5. Color Keys Row (Red, Green, Yellow, Blue)
                    ColorBar(
                        onKeyClick = { key -> viewModel.sendKey(key) }
                    )

                    // 6. Collapsible Numeric Keypad
                    NumericPad(
                        expanded = uiState.isNumpadExpanded,
                        onKeyClick = { key -> viewModel.sendKey(key) }
                    )

                    Spacer(modifier = Modifier.height(16.dp))
                }

                // Settings Modal Sheet/Dialog
                if (uiState.showSettings) {
                    SettingsDialog(
                        initialSettings = settingsState,
                        onSave = { updated -> viewModel.saveSettings(updated) },
                        onDismiss = { viewModel.toggleSettings(false) }
                    )
                }
            }
        }
    }
}

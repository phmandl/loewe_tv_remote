package at.phman.loeweremote

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import at.phman.loeweremote.ui.components.ColorBar
import at.phman.loeweremote.ui.components.ConsoleLogBar
import at.phman.loeweremote.ui.components.DPadControl
import at.phman.loeweremote.ui.components.HeaderBar
import at.phman.loeweremote.ui.components.NumericPad
import at.phman.loeweremote.ui.components.RockerControl
import at.phman.loeweremote.ui.components.TopControls
import at.phman.loeweremote.ui.dialogs.SettingsDialog
import at.phman.loeweremote.ui.theme.AnthropicBg
import at.phman.loeweremote.ui.theme.AnthropicMuted
import at.phman.loeweremote.ui.theme.LoeweRemoteTheme
import at.phman.loeweremote.viewmodel.RemoteViewModel

@Composable
fun App(
    viewModel: RemoteViewModel = viewModel { RemoteViewModel() }
) {
    val uiState by viewModel.uiState.collectAsState()
    val settingsState by viewModel.settingsState.collectAsState()

    LoeweRemoteTheme {
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .background(AnthropicBg),
            color = AnthropicBg
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .statusBarsPadding()
                    .navigationBarsPadding()
            ) {
                BoxWithConstraints(
                    modifier = Modifier.fillMaxSize()
                ) {
                    val availableHeight = maxHeight
                    val availableWidth = maxWidth

                    // Dynamically calculate component dimensions to guarantee 100% screen containment with 0 scroll/bounce
                    val isSmallScreen = availableHeight < 680.dp
                    val topBtnSize = if (isSmallScreen) 50.dp else 56.dp
                    val rockerHeight = if (isSmallScreen) 104.dp else 118.dp
                    val colorBarHeight = if (isSmallScreen) 30.dp else 34.dp
                    val logBarHeight = if (isSmallScreen) 34.dp else 38.dp
                    val rockerVerticalSpace = if (isSmallScreen) 10.dp else 14.dp
                    val topToDpadGap = if (isSmallScreen) 6.dp else 10.dp

                    // Fixed vertical space taken by Header, Log, TopControls, Rockers, ColorBar, Quote, paddings
                    val numpadHeightNeeded = if (uiState.isNumpadExpanded) (if (isSmallScreen) 125.dp else 140.dp) else 0.dp
                    val nonDpadHeight = 44.dp /*Header*/ +
                            3.dp +
                            logBarHeight +
                            6.dp +
                            topBtnSize +
                            topToDpadGap +
                            (rockerVerticalSpace * 2) /*Above & below rockers*/ +
                            rockerHeight +
                            colorBarHeight +
                            (if (uiState.isNumpadExpanded) 6.dp else 0.dp) +
                            numpadHeightNeeded +
                            26.dp /*Quote*/ +
                            16.dp /*Min bottom clearance*/

                    val availableForDpad = (availableHeight - nonDpadHeight)
                    // In DPadControl, total height is approximately dpadDiameter * 1.22f + 8.dp
                    val maxDpadWidth = (availableWidth * 0.70f).coerceIn(150.dp, 215.dp)
                    val calculatedDpadDiameter = ((availableForDpad - 8.dp) / 1.22f).coerceIn(110.dp, maxDpadWidth)

                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 6.dp, vertical = 2.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // --- TOP CLUSTER (Tight, cohesive flow) ---
                        // 1. Header Bar: Connection Status + WoL + Settings (Enlarged)
                        HeaderBar(
                            connectionState = uiState.connectionState,
                            tvChassis = uiState.tvChassis,
                            tvPowerState = uiState.tvPowerState,
                            isWolSending = uiState.isWolSending,
                            isSendingCommand = uiState.isSendingCommand,
                            onStatusClick = { viewModel.connect() },
                            onWolClick = { viewModel.sendWakeOnLan() },
                            onSettingsClick = { viewModel.toggleSettings(true) }
                        )

                        Spacer(modifier = Modifier.height(3.dp))

                        // 1b. Live Debug Terminal (Hermes console) if enabled, otherwise blank spacer
                        if (settingsState.showConsoleLog) {
                            ConsoleLogBar(
                                logs = uiState.logs,
                                onClear = { viewModel.clearLogs() },
                                height = logBarHeight
                            )
                        } else {
                            Spacer(modifier = Modifier.height(logBarHeight))
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        // 2. Top Controls: Power, Info, Menu, Mute (Dedicated top-right mute with live state)
                        TopControls(
                            onKeyClick = { key -> viewModel.sendKey(key) },
                            isMuted = uiState.isMuted,
                            buttonSize = topBtnSize
                        )

                        // 1) Slight gap to the D-Pad
                        Spacer(modifier = Modifier.height(topToDpadGap))

                        // 3. Navigation D-Pad & Back / Home
                        DPadControl(
                            onKeyClick = { key -> viewModel.sendKey(key) },
                            dpadSize = calculatedDpadDiameter
                        )

                        // Equal spacing above the Rockers
                        Spacer(modifier = Modifier.height(rockerVerticalSpace))

                        // 4. Volume & Channel Rockers + EPG / Numpad (with live volume & continuous hold)
                        RockerControl(
                            onKeyClick = { key -> viewModel.sendKey(key) },
                            onVolumeStep = { delta -> viewModel.stepVolume(delta) },
                            volume = uiState.volume,
                            onToggleNumpad = { viewModel.toggleNumpad() },
                            isNumpadExpanded = uiState.isNumpadExpanded,
                            rockerHeight = rockerHeight
                        )

                        // Equal spacing below the Rockers
                        Spacer(modifier = Modifier.height(rockerVerticalSpace))

                        // 5. Color Keys Row
                        ColorBar(
                            onKeyClick = { key -> viewModel.sendKey(key) },
                            barHeight = colorBarHeight
                        )

                        // 6. Collapsible Numeric Keypad (if expanded)
                        if (uiState.isNumpadExpanded) {
                            Spacer(modifier = Modifier.height(6.dp))
                            NumericPad(
                                expanded = uiState.isNumpadExpanded,
                                onKeyClick = { key -> viewModel.sendKey(key) },
                                buttonHeight = if (isSmallScreen) 32.dp else 36.dp
                            )
                        }

                        // 2) When the screen is large, introduce gaps ONLY at the bottom to the quote
                        Spacer(modifier = Modifier.weight(1f, fill = true))

                        // 7. Subtle Quote
                        Text(
                            text = "„Lob ist wie die Lautstärke-Taste: Drehst du sie zu weit auf, wird das Programm auch nicht besser.“",
                            color = AnthropicMuted.copy(alpha = 0.65f),
                            fontSize = 10.sp,
                            fontFamily = FontFamily.Serif,
                            fontStyle = FontStyle.Italic,
                            textAlign = TextAlign.Center,
                            lineHeight = 13.sp,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 18.dp, vertical = 2.dp)
                        )

                        Spacer(modifier = Modifier.height(2.dp))
                    }
                }

                // Settings Modal Sheet/Dialog
                if (uiState.showSettings) {
                    SettingsDialog(
                        initialSettings = settingsState,
                        tvChassis = uiState.tvChassis,
                        tvSoftwareVersion = uiState.tvSoftwareVersion,
                        tvWolEnable = uiState.tvWolEnable,
                        tvWolInteractive = uiState.tvWolInteractive,
                        onSaveTvWolSettings = { wolEnable, wolInteractive ->
                            viewModel.setTvWolSettings(wolEnable, wolInteractive)
                        },
                        onSave = { updated -> viewModel.saveSettings(updated) },
                        onDismiss = { viewModel.toggleSettings(false) }
                    )
                }
            }
        }
    }
}




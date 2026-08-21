package at.phman.loeweremote.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import at.phman.loeweremote.model.ConnectionState
import at.phman.loeweremote.model.LoeweKey
import at.phman.loeweremote.model.RemoteSettingsState
import at.phman.loeweremote.model.RemoteUiState
import at.phman.loeweremote.network.LoeweSoapClient
import at.phman.loeweremote.network.WolHelper
import at.phman.loeweremote.storage.RemoteSettings
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class RemoteViewModel(
    private val settingsRepository: RemoteSettings = RemoteSettings(),
    private val soapClient: LoeweSoapClient = LoeweSoapClient()
) : ViewModel() {

    private val _settingsState = MutableStateFlow(settingsRepository.getSettings())
    val settingsState: StateFlow<RemoteSettingsState> = _settingsState.asStateFlow()

    private val _uiState = MutableStateFlow(RemoteUiState())
    val uiState: StateFlow<RemoteUiState> = _uiState.asStateFlow()

    private var statusClearJob: Job? = null
    private var volumeDebounceJob: Job? = null

    init {
        // Initial silent check/handshake if IP is configured
        if (_settingsState.value.ipAddress.isNotBlank()) {
            connect(silent = true)
        }
    }

    /**
     * Connect & Authenticate with Loewe TV via RequestAccess SOAP API.
     */
    fun connect(silent: Boolean = false) {
        viewModelScope.launch {
            if (!silent) {
                _uiState.update { it.copy(connectionState = ConnectionState.Connecting) }
                log("Connecting to ${_settingsState.value.ipAddress}:${_settingsState.value.port}...")
            }
            val result = soapClient.requestAccess(_settingsState.value)
            result.onSuccess { session ->
                _uiState.update {
                    it.copy(
                        connectionState = ConnectionState.Connected(session.clientId),
                        statusMessage = if (!silent) "Connected (${session.clientId})" else null
                    )
                }
                log("Connected! Session: ${session.clientId} (fcid=${session.fcid})")
                scheduleStatusClear()

                // Execute Phase 1 automatic discovery and initial state refresh
                refreshDeviceData()
                refreshTvState()
            }.onFailure { error ->
                _uiState.update {
                    it.copy(
                        connectionState = ConnectionState.Error(error.message ?: "Connection failed"),
                        statusMessage = if (!silent) "Connection error: ${error.message}" else null
                    )
                }
                log("Connection error: ${error.message}")
                scheduleStatusClear()
            }
        }
    }

    /**
     * Phase 1: Query TV hardware metadata and auto-populate MAC address for Wake-on-LAN.
     */
    fun refreshDeviceData() {
        viewModelScope.launch {
            val result = soapClient.getDeviceData(_settingsState.value)
            result.onSuccess { data ->
                _uiState.update {
                    it.copy(
                        tvChassis = data.chassis,
                        tvSoftwareVersion = data.swVersion,
                        tvNetworkHostName = data.networkHostName
                    )
                }
                log("Hardware: ${data.displayModel}, SW: ${data.swVersion ?: "N/A"}")

                // Auto-save discovered MAC address
                val autoMac = data.preferredMacAddress
                if (!autoMac.isNullOrBlank()) {
                    val updatedSettings = _settingsState.value.copy(
                        autoMacAddress = autoMac,
                        macAddress = if (_settingsState.value.autoMacDiscovery) autoMac else _settingsState.value.macAddress
                    )
                    settingsRepository.saveSettings(updatedSettings)
                    _settingsState.value = updatedSettings
                    log("Discovered TV MAC: $autoMac (Auto-mode: ${_settingsState.value.autoMacDiscovery})")
                }
            }.onFailure { error ->
                log("Device discovery warning: ${error.message}")
            }
        }
    }

    /**
     * Phase 1: Refresh live TV power, playback status, volume level, and mute state.
     */
    fun refreshTvState() {
        viewModelScope.launch {
            // 1. Live status & power
            soapClient.getCurrentStatus(_settingsState.value).onSuccess { status ->
                _uiState.update {
                    it.copy(tvPowerState = status.power)
                }
            }

            // 2. Volume level
            soapClient.getVolume(_settingsState.value).onSuccess { vol ->
                _uiState.update {
                    it.copy(volume = vol)
                }
            }

            // 3. Mute state
            soapClient.getMute(_settingsState.value).onSuccess { muted ->
                _uiState.update {
                    it.copy(isMuted = muted)
                }
            }

            // 4. TV System Settings (WolEnable, WolInteractive)
            soapClient.getSettings(_settingsState.value).onSuccess { tvSettings ->
                _uiState.update {
                    it.copy(
                        tvWolEnable = tvSettings.wolEnable,
                        tvWolInteractive = tvSettings.wolInteractive
                    )
                }
                if (tvSettings.wolEnable == false) {
                    log("TV Diagnostic: Wake on LAN is disabled on TV")
                } else if (tvSettings.wolInteractive == false) {
                    log("TV Diagnostic: WoL is Standby-only (WolInteractive=0)")
                } else if (tvSettings.isWolOptimal) {
                    log("TV Diagnostic: Wake on LAN is 100% active & interactive")
                }
            }
        }
    }

    /**
     * Update TV Wake on LAN settings directly on the TV via SetSetting SOAP API.
     */
    fun setTvWolSettings(wolEnable: Boolean, wolInteractive: Boolean) {
        viewModelScope.launch {
            _uiState.update {
                it.copy(tvWolEnable = wolEnable, tvWolInteractive = wolInteractive)
            }
            log("Configuring TV: WolEnable=$wolEnable, WolInteractive=$wolInteractive...")
            val r1 = soapClient.setSetting("WolEnable", if (wolEnable) "1" else "0", _settingsState.value)
            val r2 = soapClient.setSetting("WolInteractive", if (wolInteractive) "1" else "0", _settingsState.value)
            if (r1.isSuccess && r2.isSuccess) {
                log("TV WoL Settings successfully updated on TV!")
            } else {
                log("Warning updating TV WoL settings: ${r1.exceptionOrNull()?.message ?: r2.exceptionOrNull()?.message}")
            }
            refreshTvState()
        }
    }

    /**
     * Phase 1: Step volume up/down with optimistic 0ms UI update and debounced network call.
     */
    fun stepVolume(delta: Int) {
        val currentVol = _uiState.value.volume ?: 25
        val target = (currentVol + delta).coerceIn(0, 100)
        _uiState.update { it.copy(volume = target, isMuted = false) }
        log("Vol: $target")
        scheduleSetVolume(target)
    }

    /**
     * Phase 1: Direct volume setting.
     */
    fun setVolume(targetVolume: Int) {
        val target = targetVolume.coerceIn(0, 100)
        _uiState.update { it.copy(volume = target, isMuted = false) }
        log("Set Vol: $target")
        scheduleSetVolume(target)
    }

    private fun scheduleSetVolume(targetVolume: Int) {
        volumeDebounceJob?.cancel()
        volumeDebounceJob = viewModelScope.launch {
            delay(160) // 160ms debounce to prevent rapid network spam
            val result = soapClient.setVolume(targetVolume, _settingsState.value)
            result.onFailure { err ->
                log("Volume adjust failed: ${err.message}")
            }
        }
    }

    /**
     * Phase 1: Toggle mute deterministically via GetMute / SetMute.
     */
    fun toggleMute() {
        val targetMuted = !_uiState.value.isMuted
        _uiState.update { it.copy(isMuted = targetMuted) }
        log(if (targetMuted) "Muting TV..." else "Unmuting TV...")

        viewModelScope.launch {
            val result = soapClient.setMute(targetMuted, _settingsState.value)
            result.onSuccess {
                log(if (targetMuted) "TV Muted (Active)" else "TV Unmuted")
            }.onFailure { err ->
                // Fallback to sending standard Mute key for older chassis
                soapClient.sendKey(LoeweKey.MUTE, _settingsState.value)
                log("Mute toggle sent: ${err.message}")
            }
        }
    }

    /**
     * Send remote control key to TV.
     */
    fun sendKey(key: LoeweKey) {
        // Intercept Mute key from TopControls to use two-way toggleMute()
        if (key == LoeweKey.MUTE) {
            toggleMute()
            return
        }

        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isSendingCommand = true,
                    lastSentKey = key
                )
            }
            log("Sending: ${key.label} (code=${key.code}, ${key.alphabet})")

            val result = soapClient.sendKey(key, _settingsState.value)
            result.onSuccess {
                val activeClient = soapClient.currentClientId ?: "OK"
                _uiState.update {
                    it.copy(
                        connectionState = ConnectionState.Connected(activeClient),
                        isSendingCommand = false
                    )
                }
                log("OK: ${key.label} sent successfully")

                // Update state on power or volume key changes
                if (key == LoeweKey.POWER || key == LoeweKey.TV_ON || key == LoeweKey.TV_OFF) {
                    delay(1200)
                    refreshTvState()
                }
            }.onFailure { error ->
                // Smart Fallback: If Power button was pressed and TV is in deep standby (SOAP unreachable),
                // automatically broadcast Wake-on-LAN if MAC address is available.
                if ((key == LoeweKey.POWER || key == LoeweKey.TV_ON) && _settingsState.value.effectiveMacAddress.isNotBlank()) {
                    _uiState.update { it.copy(isSendingCommand = false) }
                    log("TV unreachable via SOAP (sleep mode). Automatically broadcasting Wake-on-LAN...")
                    sendWakeOnLan()
                    return@launch
                }

                _uiState.update {
                    it.copy(
                        connectionState = ConnectionState.Error(error.message ?: "Command failed"),
                        isSendingCommand = false,
                        statusMessage = "Key ${key.label} failed: ${error.message}"
                    )
                }
                log("Error sending ${key.label}: ${error.message}")
                scheduleStatusClear()
            }
        }
    }

    /**
     * Send raw integer KeyCode.
     */
    fun sendKeyCode(code: Int) {
        viewModelScope.launch {
            _uiState.update { it.copy(isSendingCommand = true) }
            log("Sending raw keycode: $code")
            val result = soapClient.sendKeyCode(code, _settingsState.value)
            result.onSuccess {
                val activeClient = soapClient.currentClientId ?: "OK"
                _uiState.update {
                    it.copy(
                        connectionState = ConnectionState.Connected(activeClient),
                        isSendingCommand = false
                    )
                }
                log("OK: Raw keycode $code sent")
            }.onFailure { error ->
                _uiState.update {
                    it.copy(
                        connectionState = ConnectionState.Error(error.message ?: "Command failed"),
                        isSendingCommand = false,
                        statusMessage = "Key $code failed: ${error.message}"
                    )
                }
                log("Error sending raw keycode $code: ${error.message}")
                scheduleStatusClear()
            }
        }
    }

    /**
     * Broadcast Wake-on-LAN Magic Packet to wake the Loewe TV from Standby.
     */
    fun sendWakeOnLan() {
        val mac = _settingsState.value.effectiveMacAddress.trim()
        if (mac.isBlank()) {
            _uiState.update {
                it.copy(
                    statusMessage = "Please configure TV MAC address in Settings for Wake-on-LAN",
                    showSettings = true
                )
            }
            log("WoL failed: No MAC address configured")
            scheduleStatusClear(5000)
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isWolSending = true, statusMessage = "Broadcasting Wake-on-LAN...") }
            log("Broadcasting Wake-on-LAN magic packet to $mac (Port 9)...")
            val targetIp = _settingsState.value.ipAddress.trim()
            val result = WolHelper.sendWakeOnLan(mac, targetIp = targetIp)
            result.onSuccess {
                _uiState.update {
                    it.copy(
                        isWolSending = false,
                        statusMessage = "Wake-on-LAN packet sent to $mac"
                    )
                }
                log("WoL Magic Packet sent to $mac. Waiting 3s for TV network stack...")
                delay(3000)
                connect(silent = true)
            }.onFailure { error ->
                _uiState.update {
                    it.copy(
                        isWolSending = false,
                        statusMessage = "WoL failed: ${error.message}"
                    )
                }
                log("WoL failed: ${error.message}")
            }
            scheduleStatusClear()
        }
    }

    fun clearLogs() {
        _uiState.update { it.copy(logs = emptyList()) }
    }

    private fun log(message: String) {
        _uiState.update { current ->
            val updated = (current.logs + message).takeLast(40)
            current.copy(logs = updated)
        }
    }

    /**
     * Save new TV settings & re-initiate connection.
     */
    fun saveSettings(newSettings: RemoteSettingsState) {
        settingsRepository.saveSettings(newSettings)
        _settingsState.value = newSettings
        soapClient.clearSession()
        _uiState.update { it.copy(showSettings = false) }
        connect()
    }

    fun toggleSettings(show: Boolean) {
        _uiState.update { it.copy(showSettings = show) }
    }

    fun toggleNumpad() {
        _uiState.update { it.copy(isNumpadExpanded = !it.isNumpadExpanded) }
    }

    fun dismissStatus() {
        statusClearJob?.cancel()
        _uiState.update { it.copy(statusMessage = null) }
    }

    private fun scheduleStatusClear(delayMillis: Long = 3500) {
        statusClearJob?.cancel()
        statusClearJob = viewModelScope.launch {
            delay(delayMillis)
            _uiState.update { it.copy(statusMessage = null) }
        }
    }
}

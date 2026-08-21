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
     * Send remote control key to TV.
     */
    fun sendKey(key: LoeweKey) {
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
            }.onFailure { error ->
                // Smart Fallback: If Power button was pressed and TV is in deep standby (SOAP unreachable),
                // automatically broadcast Wake-on-LAN if MAC address is available.
                if ((key == LoeweKey.POWER || key == LoeweKey.TV_ON) && _settingsState.value.macAddress.isNotBlank()) {
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
        val mac = _settingsState.value.macAddress.trim()
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
                // Try connecting after a brief delay for the TV network stack to boot
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
        val timestamp = "" // Kotlin Multiplatform friendly
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

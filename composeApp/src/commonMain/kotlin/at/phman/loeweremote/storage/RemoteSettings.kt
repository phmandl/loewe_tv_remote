package at.phman.loeweremote.storage

import com.russhwolf.settings.Settings
import com.russhwolf.settings.get
import com.russhwolf.settings.set
import at.phman.loeweremote.model.RemoteSettingsState

class RemoteSettings(
    private val settings: Settings = Settings()
) {
    companion object {
        private const val KEY_IP_ADDRESS = "loewe_tv_ip"
        private const val KEY_MAC_ADDRESS = "loewe_tv_mac"
        private const val KEY_MANUAL_MAC_ADDRESS = "loewe_manual_mac"
        private const val KEY_AUTO_MAC_ADDRESS = "loewe_auto_mac"
        private const val KEY_AUTO_MAC_DISCOVERY = "loewe_auto_mac_discovery"
        private const val KEY_PORT = "loewe_tv_port"
        private const val KEY_DEVICE_ID = "loewe_device_id"
        private const val KEY_DEVICE_NAME = "loewe_device_name"
        private const val KEY_SHOW_CONSOLE_LOG = "loewe_show_console_log"

        private const val DEFAULT_IP = "192.168.1.100"
        private const val DEFAULT_MAC = ""
        private const val DEFAULT_PORT = 905
        private const val DEFAULT_DEVICE_ID = "KMP-Loewe-Client-01"
        private const val DEFAULT_DEVICE_NAME = "Loewe Remote App"
        private const val DEFAULT_SHOW_CONSOLE_LOG = false
        private const val DEFAULT_AUTO_MAC_DISCOVERY = true
    }

    fun getSettings(): RemoteSettingsState {
        return runCatching {
            val ip = settings.getString(KEY_IP_ADDRESS, DEFAULT_IP)
            val mac = settings.getString(KEY_MAC_ADDRESS, DEFAULT_MAC)
            val manualMac = settings.getString(KEY_MANUAL_MAC_ADDRESS, mac)
            val autoMac = settings.getString(KEY_AUTO_MAC_ADDRESS, "")
            val autoMacDiscovery = settings.getBoolean(KEY_AUTO_MAC_DISCOVERY, DEFAULT_AUTO_MAC_DISCOVERY)
            val port = settings.getInt(KEY_PORT, DEFAULT_PORT)
            val deviceId = settings.getString(KEY_DEVICE_ID, DEFAULT_DEVICE_ID)
            val deviceName = settings.getString(KEY_DEVICE_NAME, DEFAULT_DEVICE_NAME)
            val showConsoleLog = settings.getBoolean(KEY_SHOW_CONSOLE_LOG, DEFAULT_SHOW_CONSOLE_LOG)

            RemoteSettingsState(
                ipAddress = ip,
                macAddress = mac,
                manualMacAddress = manualMac,
                autoMacAddress = autoMac,
                autoMacDiscovery = autoMacDiscovery,
                port = port,
                deviceId = deviceId,
                deviceName = deviceName,
                showConsoleLog = showConsoleLog
            )
        }.getOrDefault(
            RemoteSettingsState(
                ipAddress = DEFAULT_IP,
                macAddress = DEFAULT_MAC,
                manualMacAddress = DEFAULT_MAC,
                autoMacAddress = "",
                autoMacDiscovery = DEFAULT_AUTO_MAC_DISCOVERY,
                port = DEFAULT_PORT,
                deviceId = DEFAULT_DEVICE_ID,
                deviceName = DEFAULT_DEVICE_NAME,
                showConsoleLog = DEFAULT_SHOW_CONSOLE_LOG
            )
        )
    }

    fun saveSettings(state: RemoteSettingsState) {
        runCatching {
            settings[KEY_IP_ADDRESS] = state.ipAddress.trim()
            settings[KEY_MAC_ADDRESS] = state.effectiveMacAddress.trim()
            settings[KEY_MANUAL_MAC_ADDRESS] = state.manualMacAddress.trim()
            settings[KEY_AUTO_MAC_ADDRESS] = state.autoMacAddress.trim()
            settings[KEY_AUTO_MAC_DISCOVERY] = state.autoMacDiscovery
            settings[KEY_PORT] = state.port
            settings[KEY_DEVICE_ID] = state.deviceId.trim()
            settings[KEY_DEVICE_NAME] = state.deviceName.trim()
            settings[KEY_SHOW_CONSOLE_LOG] = state.showConsoleLog
        }
    }
}

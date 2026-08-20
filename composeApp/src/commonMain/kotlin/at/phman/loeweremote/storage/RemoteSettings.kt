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
        private const val KEY_PORT = "loewe_tv_port"
        private const val KEY_DEVICE_ID = "loewe_device_id"
        private const val KEY_DEVICE_NAME = "loewe_device_name"

        private const val DEFAULT_IP = "192.168.1.100"
        private const val DEFAULT_MAC = ""
        private const val DEFAULT_PORT = 905
        private const val DEFAULT_DEVICE_ID = "KMP-Loewe-Client-01"
        private const val DEFAULT_DEVICE_NAME = "Loewe Remote App"
    }

    fun getSettings(): RemoteSettingsState {
        return runCatching {
            val ip = settings.getString(KEY_IP_ADDRESS, DEFAULT_IP)
            val mac = settings.getString(KEY_MAC_ADDRESS, DEFAULT_MAC)
            val port = settings.getInt(KEY_PORT, DEFAULT_PORT)
            val deviceId = settings.getString(KEY_DEVICE_ID, DEFAULT_DEVICE_ID)
            val deviceName = settings.getString(KEY_DEVICE_NAME, DEFAULT_DEVICE_NAME)

            RemoteSettingsState(
                ipAddress = ip,
                macAddress = mac,
                port = port,
                deviceId = deviceId,
                deviceName = deviceName
            )
        }.getOrDefault(
            RemoteSettingsState(
                ipAddress = DEFAULT_IP,
                macAddress = DEFAULT_MAC,
                port = DEFAULT_PORT,
                deviceId = DEFAULT_DEVICE_ID,
                deviceName = DEFAULT_DEVICE_NAME
            )
        )
    }

    fun saveSettings(state: RemoteSettingsState) {
        runCatching {
            settings[KEY_IP_ADDRESS] = state.ipAddress.trim()
            settings[KEY_MAC_ADDRESS] = state.macAddress.trim()
            settings[KEY_PORT] = state.port
            settings[KEY_DEVICE_ID] = state.deviceId.trim()
            settings[KEY_DEVICE_NAME] = state.deviceName.trim()
        }
    }
}

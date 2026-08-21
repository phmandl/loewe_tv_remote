package at.phman.loeweremote.model

data class RemoteSettingsState(
    val ipAddress: String = "192.168.1.100",
    val macAddress: String = "",
    val manualMacAddress: String = "",
    val autoMacAddress: String = "",
    val autoMacDiscovery: Boolean = true,
    val port: Int = 905,
    val deviceType: String = "KMP Native Remote",
    val deviceId: String = "KMP-Loewe-Client-01",
    val deviceName: String = "Loewe Remote App",
    val showConsoleLog: Boolean = false
) {
    val endpointUrl: String
        get() = "http://${ipAddress.trim()}:$port/loewe_tablet_0001"

    /**
     * The active MAC address to use for Wake-on-LAN and device UUID generation.
     */
    val effectiveMacAddress: String
        get() = if (autoMacDiscovery) {
            autoMacAddress.ifBlank { macAddress }
        } else {
            manualMacAddress.ifBlank { macAddress }
        }
}

package at.phman.loeweremote.model

data class RemoteSettingsState(
    val ipAddress: String = "192.168.1.100",
    val macAddress: String = "",
    val port: Int = 905,
    val deviceType: String = "KMP Native Remote",
    val deviceId: String = "KMP-Loewe-Client-01",
    val deviceName: String = "Loewe Remote App"
) {
    val endpointUrl: String
        get() = "http://${ipAddress.trim()}:$port/loewe_tablet_0001"
}

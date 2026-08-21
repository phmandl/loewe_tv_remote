package at.phman.loeweremote.model

/**
 * Device metadata returned by Loewe TV GetDeviceData API (Sec 9.2).
 */
data class LoeweDeviceData(
    val chassis: String? = null,
    val swVersion: String? = null,
    val macAddressLan: String? = null,
    val macAddressWlan: String? = null,
    val macAddress: String? = null,
    val location: String? = null,
    val networkHostName: String? = null,
    val streamingServerName: String? = null
) {
    /**
     * Best available MAC address for Wake-on-LAN (prefers wired LAN over generic/WLAN).
     */
    val preferredMacAddress: String?
        get() = macAddressLan?.takeIf { it.isNotBlank() }
            ?: macAddress?.takeIf { it.isNotBlank() }
            ?: macAddressWlan?.takeIf { it.isNotBlank() }

    /**
     * Clean display name for TV (e.g. "Loewe (SL420)").
     */
    val displayModel: String
        get() = when {
            chassis != null -> "Loewe ($chassis)"
            networkHostName != null -> networkHostName
            else -> "Loewe TV"
        }
}

/**
 * Live playback & power status returned by GetCurrentStatus API (Sec 9.11).
 */
data class LoeweTvStatus(
    val power: String? = null, // "tv" (active) or "idle" (network standby)
    val hdrPlayerState: String? = null, // "idle", "playback", "timeshift_playback", "media_prepare"
    val hdrSpeed: String? = null, // "play", "pause", "forw_fast_max", etc.
    val systemLocked: String? = null // "unlocked", "locked"
) {
    val isPowerActive: Boolean
        get() = power?.equals("tv", ignoreCase = true) == true

    val isStandby: Boolean
        get() = power?.equals("idle", ignoreCase = true) == true
}

/**
 * TV Settings returned by GetSettings API (Sec 9.19).
 */
data class LoeweTvSettings(
    val wolEnable: Boolean? = null,
    val wolInteractive: Boolean? = null,
    val multiroomActive: Boolean? = null,
    val networkHostName: String? = null
) {
    val isWolOptimal: Boolean
        get() = wolEnable == true && wolInteractive == true

    val isWolStandbyOnly: Boolean
        get() = wolEnable == true && wolInteractive != true

    val isWolDisabled: Boolean
        get() = wolEnable == false
}

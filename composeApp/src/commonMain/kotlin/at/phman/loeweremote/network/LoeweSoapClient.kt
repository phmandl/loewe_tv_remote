package at.phman.loeweremote.network

import at.phman.loeweremote.model.LoeweDeviceData
import at.phman.loeweremote.model.LoeweKey
import at.phman.loeweremote.model.LoeweTvSettings
import at.phman.loeweremote.model.LoeweTvStatus
import at.phman.loeweremote.model.RemoteSettingsState
import io.ktor.client.HttpClient
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.request.headers
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.contentType
import io.ktor.http.isSuccess
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

data class LoeweSession(
    val clientId: String,
    val fcid: String
)

class LoeweSoapClient(
    private val httpClient: HttpClient = createDefaultHttpClient()
) {
    private val mutex = Mutex()
    private var activeSession: LoeweSession? = null

    val currentClientId: String?
        get() = activeSession?.clientId

    val currentSession: LoeweSession?
        get() = activeSession

    /**
     * Clear cached session (e.g. on disconnect or TV IP change).
     */
    fun clearSession() {
        activeSession = null
    }

    /**
     * Request access to the Loewe TV (Sec 9.1).
     */
    suspend fun requestAccess(settings: RemoteSettingsState): Result<LoeweSession> = mutex.withLock {
        performRequestAccess(settings)
    }

    private suspend fun performRequestAccess(settings: RemoteSettingsState): Result<LoeweSession> = runCatching {
        val url = settings.endpointUrl
        val payload = buildRequestAccessPayload(
            deviceName = settings.deviceName,
            deviceUuid = settings.effectiveMacAddress.replace(":", "").replace("-", "").ifBlank { "001122334455" }
        )

        val response: HttpResponse = httpClient.post(url) {
            contentType(ContentType.parse("text/xml; charset=utf-8"))
            headers {
                append("Accept", "*/*")
                append("SOAPAction", "RequestAccess")
                append(HttpHeaders.UserAgent, "LoeweRemote-KMP/1.0")
            }
            setBody(payload)
        }

        val body = response.bodyAsText()
        if (!response.status.isSuccess()) {
            throw Exception("HTTP ${response.status.value}: ${response.status.description}\n$body")
        }

        val clientId = extractTagValue(body, "ClientId")
            ?: throw Exception("No <ClientId> found in TV response:\n$body")

        val fcid = extractTagValue(body, "fcid") ?: "1"

        val session = LoeweSession(clientId = clientId, fcid = fcid)
        activeSession = session
        session
    }

    /**
     * Ingest a remote control key command to the TV (Sec 9.4.1).
     * Automatically attempts handshake if not connected or if session expired.
     */
    suspend fun sendKey(key: LoeweKey, settings: RemoteSettingsState): Result<Unit> {
        return sendKeyCode(key.code, settings, alphabet = key.alphabet)
    }

    /**
     * Ingest a raw integer key code to the TV (Sec 9.4.1).
     */
    suspend fun sendKeyCode(
        keyCode: Int,
        settings: RemoteSettingsState,
        alphabet: String = "l2700"
    ): Result<Unit> {
        return executeSoapAction(
            action = "InjectRCKey",
            settings = settings,
            payloadBuilder = { session ->
                buildInjectKeyPayload(session.fcid, session.clientId, keyCode, alphabet)
            }
        ).map { }
    }

    /**
     * Retrieve hardware info, MAC addresses, and chassis data from TV (Sec 9.2).
     */
    suspend fun getDeviceData(settings: RemoteSettingsState): Result<LoeweDeviceData> {
        return executeSoapAction(
            action = "GetDeviceData",
            settings = settings,
            payloadBuilder = { session ->
                buildGetDeviceDataPayload(session.fcid, session.clientId)
            }
        ).map { xml ->
            LoeweDeviceData(
                chassis = extractTagValue(xml, "Chassis"),
                swVersion = extractTagValue(xml, "SW-Version"),
                macAddressLan = extractTagValue(xml, "MAC-Address-LAN"),
                macAddressWlan = extractTagValue(xml, "MAC-Address-WLAN"),
                macAddress = extractTagValue(xml, "MAC-Address"),
                location = extractTagValue(xml, "Location"),
                networkHostName = extractTagValue(xml, "NetworkHostName"),
                streamingServerName = extractTagValue(xml, "StreamingServerName")
            )
        }
    }

    /**
     * Retrieve current TV volume level (0–100%) (Sec 9.4.5).
     */
    suspend fun getVolume(settings: RemoteSettingsState): Result<Int> {
        return executeSoapAction(
            action = "GetVolume",
            settings = settings,
            payloadBuilder = { session ->
                buildGetVolumePayload(session.fcid, session.clientId)
            }
        ).map { xml ->
            val rawValue = extractTagValue(xml, "Value")?.toIntOrNull() ?: 0
            (rawValue / 10000).coerceIn(0, 100)
        }
    }

    /**
     * Set absolute TV volume level (0–100%) (Sec 9.4.5).
     */
    suspend fun setVolume(volume: Int, settings: RemoteSettingsState): Result<Unit> {
        val scaledValue = volume.coerceIn(0, 100) * 10000
        return executeSoapAction(
            action = "SetVolume",
            settings = settings,
            payloadBuilder = { session ->
                buildSetVolumePayload(session.fcid, session.clientId, scaledValue)
            }
        ).map { }
    }

    /**
     * Query current mute state (Sec 9.4.6).
     */
    suspend fun getMute(settings: RemoteSettingsState): Result<Boolean> {
        return executeSoapAction(
            action = "GetMute",
            settings = settings,
            payloadBuilder = { session ->
                buildGetMutePayload(session.fcid, session.clientId)
            }
        ).map { xml ->
            val rawValue = extractTagValue(xml, "Value")
            rawValue == "1"
        }
    }

    /**
     * Set explicit mute state (Sec 9.4.6).
     */
    suspend fun setMute(isMuted: Boolean, settings: RemoteSettingsState): Result<Unit> {
        return executeSoapAction(
            action = "SetMute",
            settings = settings,
            payloadBuilder = { session ->
                buildSetMutePayload(session.fcid, session.clientId, if (isMuted) 1 else 0)
            }
        ).map { }
    }

    /**
     * Query current TV power, player state, and system lock status (Sec 9.11).
     */
    suspend fun getCurrentStatus(settings: RemoteSettingsState): Result<LoeweTvStatus> {
        return executeSoapAction(
            action = "GetCurrentStatus",
            settings = settings,
            payloadBuilder = { session ->
                buildGetCurrentStatusPayload(session.fcid, session.clientId)
            }
        ).map { xml ->
            LoeweTvStatus(
                power = extractTagValue(xml, "Power"),
                hdrPlayerState = extractTagValue(xml, "HdrPlayerState"),
                hdrSpeed = extractTagValue(xml, "HdrSpeed"),
                systemLocked = extractTagValue(xml, "SystemLocked")
            )
        }
    }

    /**
     * Query TV system settings such as WolEnable, WolInteractive, Multiroom (Sec 9.19).
     */
    suspend fun getSettings(settings: RemoteSettingsState): Result<LoeweTvSettings> {
        return executeSoapAction(
            action = "GetSettings",
            settings = settings,
            payloadBuilder = { session ->
                buildGetSettingsPayload(session.fcid, session.clientId)
            }
        ).map { xml ->
            LoeweTvSettings(
                wolEnable = extractTagValue(xml, "WolEnable")?.let { it == "1" },
                wolInteractive = extractTagValue(xml, "WolInteractive")?.let { it == "1" },
                multiroomActive = extractTagValue(xml, "MultiroomActive")?.let { it == "1" },
                networkHostName = extractTagValue(xml, "NetworkHostName")
            )
        }
    }

    /**
     * Set a TV system setting such as WolEnable, WolInteractive (Sec 9.19).
     */
    suspend fun setSetting(name: String, value: String, settings: RemoteSettingsState): Result<Unit> {
        return executeSoapAction(
            action = "SetSetting",
            settings = settings,
            payloadBuilder = { session ->
                buildSetSettingPayload(session.fcid, session.clientId, name, value)
            }
        ).map { }
    }

    /**
     * Executes a SOAP action with automatic session acquisition & 1-shot retry on session timeout.
     */
    private suspend fun executeSoapAction(
        action: String,
        settings: RemoteSettingsState,
        payloadBuilder: (LoeweSession) -> String
    ): Result<String> {
        // Step 1: Ensure active session
        val session: LoeweSession = mutex.withLock {
            var current = activeSession
            if (current == null) {
                val authResult = performRequestAccess(settings)
                if (authResult.isFailure) {
                    return Result.failure(authResult.exceptionOrNull() ?: Exception("Authentication failed"))
                }
                current = authResult.getOrNull()
            }
            current ?: return Result.failure(Exception("Could not obtain session from Loewe TV"))
        }

        // Step 2: First attempt
        val firstAttempt = performHttpPost(action, payloadBuilder(session), settings)
        if (firstAttempt.isSuccess) {
            return firstAttempt
        }

        // Step 3: Re-auth and retry once on failure
        val reAuthResult = requestAccess(settings)
        if (reAuthResult.isSuccess) {
            val newSession = reAuthResult.getOrNull() ?: return Result.failure(Exception("Re-auth failed"))
            return performHttpPost(action, payloadBuilder(newSession), settings)
        }

        return firstAttempt
    }

    private suspend fun performHttpPost(
        soapAction: String,
        payload: String,
        settings: RemoteSettingsState
    ): Result<String> = runCatching {
        val url = settings.endpointUrl
        val response: HttpResponse = httpClient.post(url) {
            contentType(ContentType.parse("text/xml; charset=utf-8"))
            headers {
                append("Accept", "*/*")
                append("SOAPAction", soapAction)
                append(HttpHeaders.UserAgent, "LoeweRemote-KMP/1.0")
            }
            setBody(payload)
        }

        val body = response.bodyAsText()
        if (!response.status.isSuccess()) {
            throw Exception("HTTP ${response.status.value}: ${response.status.description}")
        }

        if (body.contains("<soap:Fault>", ignoreCase = true) ||
            body.contains("<soapenv:Fault>", ignoreCase = true) ||
            body.contains("<Fault>", ignoreCase = true)
        ) {
            throw Exception("SOAP Fault for $soapAction:\n$body")
        }

        body
    }

    private fun extractTagValue(xml: String, tag: String): String? {
        val regex = Regex("<(?:[a-zA-Z0-9_-]+:)?$tag(?:\\s+[^>]*)?>(.*?)</(?:[a-zA-Z0-9_-]+:)?$tag>", RegexOption.IGNORE_CASE)
        val match = regex.find(xml)
        return match?.groupValues?.get(1)?.trim()
    }

    private fun buildRequestAccessPayload(
        deviceName: String,
        deviceUuid: String
    ): String {
        return """<?xml version="1.0" encoding="utf-8"?>
<SOAP-ENV:Envelope xmlns:SOAP-ENV="http://schemas.xmlsoap.org/soap/envelope/"
 xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
 xmlns:xsd="http://www.w3.org/2001/XMLSchema"
 SOAP-ENV:encodingStyle="http://schemas.xmlsoap.org/soap/encoding/">
  <SOAP-ENV:Header/>
  <SOAP-ENV:Body>
    <RequestAccess xmlns="urn:loewe.de:RemoteTV:Tablet">
      <fcid>1</fcid>
      <ClientId>?</ClientId>
      <DeviceType>$deviceName</DeviceType>
      <DeviceName>$deviceName</DeviceName>
      <DeviceUUID>$deviceUuid</DeviceUUID>
      <RequesterName>$deviceName</RequesterName>
    </RequestAccess>
  </SOAP-ENV:Body>
</SOAP-ENV:Envelope>"""
    }

    private fun buildInjectKeyPayload(
        fcid: String,
        clientId: String,
        keyCode: Int,
        alphabet: String = "l2700"
    ): String {
        return """<?xml version="1.0" encoding="utf-8"?>
<soapenv:Envelope
 xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/"
 xmlns:ltv="urn:loewe.de:RemoteTV:Tablet">
  <soapenv:Header/>
  <soapenv:Body>
    <ltv:InjectRCKey>
      <ltv:fcid>$fcid</ltv:fcid>
      <ltv:ClientId>$clientId</ltv:ClientId>
      <ltv:InputEventSequence>
        <ltv:RCKeyEvent alphabet="$alphabet" value="$keyCode" mode="press"/>
        <ltv:RCKeyEvent alphabet="$alphabet" value="$keyCode" mode="release"/>
      </ltv:InputEventSequence>
    </ltv:InjectRCKey>
  </soapenv:Body>
</soapenv:Envelope>"""
    }

    private fun buildGetDeviceDataPayload(fcid: String, clientId: String): String {
        return """<?xml version="1.0" encoding="utf-8"?>
<soapenv:Envelope
 xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/"
 xmlns:ltv="urn:loewe.de:RemoteTV:Tablet">
  <soapenv:Header/>
  <soapenv:Body>
    <ltv:GetDeviceData>
      <ltv:fcid>$fcid</ltv:fcid>
      <ltv:ClientId>$clientId</ltv:ClientId>
    </ltv:GetDeviceData>
  </soapenv:Body>
</soapenv:Envelope>"""
    }

    private fun buildGetVolumePayload(fcid: String, clientId: String): String {
        return """<?xml version="1.0" encoding="utf-8"?>
<soapenv:Envelope
 xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/"
 xmlns:ltv="urn:loewe.de:RemoteTV:Tablet">
  <soapenv:Header/>
  <soapenv:Body>
    <ltv:GetVolume>
      <ltv:fcid>$fcid</ltv:fcid>
      <ltv:ClientId>$clientId</ltv:ClientId>
    </ltv:GetVolume>
  </soapenv:Body>
</soapenv:Envelope>"""
    }

    private fun buildSetVolumePayload(fcid: String, clientId: String, scaledVolume: Int): String {
        return """<?xml version="1.0" encoding="utf-8"?>
<soapenv:Envelope
 xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/"
 xmlns:ltv="urn:loewe.de:RemoteTV:Tablet">
  <soapenv:Header/>
  <soapenv:Body>
    <ltv:SetVolume>
      <ltv:fcid>$fcid</ltv:fcid>
      <ltv:ClientId>$clientId</ltv:ClientId>
      <ltv:Value>$scaledVolume</ltv:Value>
    </ltv:SetVolume>
  </soapenv:Body>
</soapenv:Envelope>"""
    }

    private fun buildGetMutePayload(fcid: String, clientId: String): String {
        return """<?xml version="1.0" encoding="utf-8"?>
<soapenv:Envelope
 xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/"
 xmlns:ltv="urn:loewe.de:RemoteTV:Tablet">
  <soapenv:Header/>
  <soapenv:Body>
    <ltv:GetMute>
      <ltv:fcid>$fcid</ltv:fcid>
      <ltv:ClientId>$clientId</ltv:ClientId>
    </ltv:GetMute>
  </soapenv:Body>
</soapenv:Envelope>"""
    }

    private fun buildSetMutePayload(fcid: String, clientId: String, muteValue: Int): String {
        return """<?xml version="1.0" encoding="utf-8"?>
<soapenv:Envelope
 xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/"
 xmlns:ltv="urn:loewe.de:RemoteTV:Tablet">
  <soapenv:Header/>
  <soapenv:Body>
    <ltv:SetMute>
      <ltv:fcid>$fcid</ltv:fcid>
      <ltv:ClientId>$clientId</ltv:ClientId>
      <ltv:Value>$muteValue</ltv:Value>
    </ltv:SetMute>
  </soapenv:Body>
</soapenv:Envelope>"""
    }

    private fun buildGetCurrentStatusPayload(fcid: String, clientId: String): String {
        return """<?xml version="1.0" encoding="utf-8"?>
<soapenv:Envelope
 xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/"
 xmlns:ltv="urn:loewe.de:RemoteTV:Tablet">
  <soapenv:Header/>
  <soapenv:Body>
    <ltv:GetCurrentStatus>
      <ltv:fcid>$fcid</ltv:fcid>
      <ltv:ClientId>$clientId</ltv:ClientId>
    </ltv:GetCurrentStatus>
  </soapenv:Body>
</soapenv:Envelope>"""
    }

    private fun buildGetSettingsPayload(fcid: String, clientId: String): String {
        return """<?xml version="1.0" encoding="utf-8"?>
<soapenv:Envelope
 xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/"
 xmlns:ltv="urn:loewe.de:RemoteTV:Tablet">
  <soapenv:Header/>
  <soapenv:Body>
    <ltv:GetSettings>
      <ltv:fcid>$fcid</ltv:fcid>
      <ltv:ClientId>$clientId</ltv:ClientId>
    </ltv:GetSettings>
  </soapenv:Body>
</soapenv:Envelope>"""
    }

    private fun buildSetSettingPayload(fcid: String, clientId: String, name: String, value: String): String {
        return """<?xml version="1.0" encoding="utf-8"?>
<soapenv:Envelope
 xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/"
 xmlns:ltv="urn:loewe.de:RemoteTV:Tablet">
  <soapenv:Header/>
  <soapenv:Body>
    <ltv:SetSetting>
      <ltv:fcid>$fcid</ltv:fcid>
      <ltv:ClientId>$clientId</ltv:ClientId>
      <ltv:Name>$name</ltv:Name>
      <ltv:Value>$value</ltv:Value>
    </ltv:SetSetting>
  </soapenv:Body>
</soapenv:Envelope>"""
    }

    companion object {
        fun createDefaultHttpClient(): HttpClient {
            return HttpClient {
                install(HttpTimeout) {
                    requestTimeoutMillis = 4000
                    connectTimeoutMillis = 2500
                    socketTimeoutMillis = 4000
                }
            }
        }
    }
}

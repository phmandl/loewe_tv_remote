package at.phman.loeweremote.network

import at.phman.loeweremote.model.LoeweKey
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
     * Request access to the Loewe TV.
     * Reference: hass-loewetv-remoteapi (custom_components/loewe/soap.py)
     */
    suspend fun requestAccess(settings: RemoteSettingsState): Result<LoeweSession> = mutex.withLock {
        runCatching {
            val url = settings.endpointUrl
            val payload = buildRequestAccessPayload(
                deviceName = settings.deviceName,
                deviceUuid = settings.macAddress.replace(":", "").replace("-", "").ifBlank { "001122334455" }
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
    }

    /**
     * Ingest a remote control key command to the TV.
     * Automatically attempts handshake if not connected or if session expired.
     */
    suspend fun sendKey(key: LoeweKey, settings: RemoteSettingsState): Result<Unit> {
        return sendKeyCode(key.code, settings, alphabet = key.alphabet)
    }

    /**
     * Ingest a raw integer key code to the TV.
     */
    suspend fun sendKeyCode(
        keyCode: Int,
        settings: RemoteSettingsState,
        alphabet: String = "l2700"
    ): Result<Unit> {
        // Step 1: Ensure we have an active session
        var session = activeSession
        if (session == null) {
            val authResult = requestAccess(settings)
            if (authResult.isFailure) {
                return Result.failure(authResult.exceptionOrNull() ?: Exception("Authentication failed"))
            }
            session = authResult.getOrNull()
        }

        if (session == null) {
            return Result.failure(Exception("Could not obtain session from Loewe TV"))
        }

        // Step 2: Attempt key injection
        val firstAttempt = executeInjectKey(keyCode, session, settings, alphabet)
        if (firstAttempt.isSuccess) {
            return Result.success(Unit)
        }

        // Step 3: Auto-reconnect & retry once if the first attempt failed
        // (TV might have been restarted or session timed out)
        val reAuthResult = requestAccess(settings)
        if (reAuthResult.isSuccess) {
            val newSession = reAuthResult.getOrNull() ?: return Result.failure(Exception("Re-auth failed"))
            return executeInjectKey(keyCode, newSession, settings, alphabet)
        }

        return Result.failure(
            firstAttempt.exceptionOrNull() ?: Exception("Failed to send key $keyCode to Loewe TV")
        )
    }

    private suspend fun executeInjectKey(
        keyCode: Int,
        session: LoeweSession,
        settings: RemoteSettingsState,
        alphabet: String = "l2700"
    ): Result<Unit> = runCatching {
        val url = settings.endpointUrl
        val payload = buildInjectKeyPayload(session.fcid, session.clientId, keyCode, alphabet)

        val response: HttpResponse = httpClient.post(url) {
            contentType(ContentType.parse("text/xml; charset=utf-8"))
            headers {
                append("Accept", "*/*")
                append("SOAPAction", "InjectRCKey")
                append(HttpHeaders.UserAgent, "LoeweRemote-KMP/1.0")
            }
            setBody(payload)
        }

        val body = response.bodyAsText()
        if (!response.status.isSuccess()) {
            throw Exception("HTTP ${response.status.value}: ${response.status.description}")
        }

        if (body.contains("<soap:Fault>", ignoreCase = true) || body.contains("<soapenv:Fault>", ignoreCase = true) || body.contains("<Fault>", ignoreCase = true)) {
            throw Exception("SOAP Fault returned by TV:\n$body")
        }
    }

    private fun extractTagValue(xml: String, tag: String): String? {
        val regex = Regex("<(?:.*:)?$tag>(.*?)</(?:.*:)?$tag>", RegexOption.IGNORE_CASE)
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
 xmlns:wsa="http://schemas.xmlsoap.org/ws/2004/08/addressing"
 xmlns:wse="http://www.w3.org/2009/02/ws-evt"
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

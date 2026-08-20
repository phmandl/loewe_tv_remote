package at.phman.loeweremote.network

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext

/**
 * Low-level platform socket dispatcher for UDP Magic Packet broadcast.
 */
expect object PlatformWolSender {
    fun sendPacket(bytes: ByteArray, port: Int = 9): Result<Unit>
}

object WolHelper {

    /**
     * Constructs a 102-byte Wake-on-LAN magic packet:
     * 6 bytes of 0xFF followed by 16 iterations of the 6-byte MAC address.
     */
    fun createMagicPacket(macAddress: String): ByteArray {
        val cleanMac = macAddress.replace(":", "").replace("-", "").trim()
        require(cleanMac.length == 12) { "Invalid MAC address format: '$macAddress'. Expected 12 hex characters." }

        val macBytes = ByteArray(6)
        for (i in 0 until 6) {
            val hex = cleanMac.substring(i * 2, i * 2 + 2)
            macBytes[i] = hex.toInt(16).toByte()
        }

        val packet = ByteArray(102)
        // 6 bytes of 0xFF
        for (i in 0 until 6) {
            packet[i] = 0xFF.toByte()
        }
        // 16 iterations of MAC address using multiplatform copyInto
        for (i in 0 until 16) {
            macBytes.copyInto(packet, destinationOffset = 6 + (i * 6), startIndex = 0, endIndex = 6)
        }

        return packet
    }

    /**
     * Broadcasts a Wake-on-LAN packet to port 9.
     */
    suspend fun sendWakeOnLan(macAddress: String, port: Int = 9): Result<Unit> = withContext(Dispatchers.IO) {
        runCatching {
            val packet = createMagicPacket(macAddress)
            val result = PlatformWolSender.sendPacket(packet, port)
            if (result.isFailure) {
                throw result.exceptionOrNull() ?: Exception("Failed to broadcast WoL packet")
            }
        }
    }
}

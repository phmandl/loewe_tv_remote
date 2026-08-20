package at.phman.loeweremote.network

import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.IntVar
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.alloc
import kotlinx.cinterop.convert
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.ptr
import kotlinx.cinterop.reinterpret
import kotlinx.cinterop.sizeOf
import kotlinx.cinterop.usePinned
import kotlinx.cinterop.value
import platform.posix.AF_INET
import platform.posix.IPPROTO_UDP
import platform.posix.SOCK_DGRAM
import platform.posix.SOL_SOCKET
import platform.posix.SO_BROADCAST
import platform.posix.close
import platform.posix.sendto
import platform.posix.setsockopt
import platform.posix.sockaddr_in
import platform.posix.socket

actual object PlatformWolSender {
    @OptIn(ExperimentalForeignApi::class)
    actual fun sendPacket(bytes: ByteArray, port: Int): Result<Unit> = runCatching {
        val sock = socket(AF_INET, SOCK_DGRAM, IPPROTO_UDP)
        if (sock < 0) {
            throw Exception("Failed to create UDP socket: error code $sock")
        }

        try {
            memScoped {
                val broadcastEnable = alloc<IntVar>()
                broadcastEnable.value = 1
                val optResult = setsockopt(
                    sock,
                    SOL_SOCKET,
                    SO_BROADCAST,
                    broadcastEnable.ptr,
                    sizeOf<IntVar>().convert()
                )
                if (optResult < 0) {
                    throw Exception("Failed to configure SO_BROADCAST on socket")
                }

                val targetAddr = alloc<sockaddr_in>()
                targetAddr.sin_len = sizeOf<sockaddr_in>().convert()
                targetAddr.sin_family = AF_INET.convert()
                targetAddr.sin_port = toBigEndian16(port).convert()
                // 255.255.255.255 broadcast address (INADDR_BROADCAST)
                targetAddr.sin_addr.s_addr = 0xFFFFFFFFu

                bytes.usePinned { pinned ->
                    val sent = sendto(
                        sock,
                        pinned.addressOf(0),
                        bytes.size.convert(),
                        0,
                        targetAddr.ptr.reinterpret(),
                        sizeOf<sockaddr_in>().convert()
                    )
                    if (sent < 0) {
                        throw Exception("Failed to send UDP packet: code $sent")
                    }
                }
            }
        } finally {
            close(sock)
        }
    }

    /**
     * Converts a 16-bit port number to Network Byte Order (Big-Endian).
     * Replaces C macro htons().
     */
    private fun toBigEndian16(port: Int): UShort {
        val b1 = (port and 0xFF) shl 8
        val b2 = (port ushr 8) and 0xFF
        return (b1 or b2).toUShort()
    }
}

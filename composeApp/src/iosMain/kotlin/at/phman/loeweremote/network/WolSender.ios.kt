package at.phman.loeweremote.network

import kotlinx.cinterop.CPointerVar
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.IntVar
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.alloc
import kotlinx.cinterop.convert
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.pointed
import kotlinx.cinterop.ptr
import kotlinx.cinterop.reinterpret
import kotlinx.cinterop.sizeOf
import kotlinx.cinterop.toKString
import kotlinx.cinterop.usePinned
import kotlinx.cinterop.value
import platform.posix.AF_INET
import platform.posix.IFF_BROADCAST
import platform.posix.IFF_LOOPBACK
import platform.posix.IFF_UP
import platform.posix.IPPROTO_UDP
import platform.posix.SOCK_DGRAM
import platform.posix.SOL_SOCKET
import platform.posix.SO_BROADCAST
import platform.posix.close
import platform.posix.errno
import platform.posix.freeifaddrs
import platform.posix.getifaddrs
import platform.posix.ifaddrs
import platform.posix.inet_addr
import platform.posix.sendto
import platform.posix.setsockopt
import platform.posix.sockaddr_in
import platform.posix.socket
import platform.posix.strerror

actual object PlatformWolSender {
    @OptIn(ExperimentalForeignApi::class)
    actual fun sendPacket(bytes: ByteArray, port: Int, targetIp: String): Result<Unit> = runCatching {
        val sock = socket(AF_INET, SOCK_DGRAM, IPPROTO_UDP)
        if (sock < 0) {
            val err = errno
            val errStr = strerror(err)?.toKString() ?: "unknown"
            throw Exception("Failed to create UDP socket: errno $err ($errStr)")
        }

        var packetsSentCount = 0
        var lastErrorMsg = ""

        try {
            memScoped {
                val broadcastEnable = alloc<IntVar>()
                broadcastEnable.value = 1
                setsockopt(
                    sock,
                    SOL_SOCKET,
                    SO_BROADCAST,
                    broadcastEnable.ptr,
                    sizeOf<IntVar>().convert()
                )

                val networkPort = toBigEndian16(port).convert<UShort>()

                fun sendToIp(ipStr: String) {
                    if (ipStr.isBlank()) return
                    val addr = alloc<sockaddr_in>()
                    addr.sin_len = sizeOf<sockaddr_in>().convert()
                    addr.sin_family = AF_INET.convert()
                    addr.sin_port = networkPort
                    addr.sin_addr.s_addr = inet_addr(ipStr)

                    bytes.usePinned { pinned ->
                        val sent = sendto(
                            sock,
                            pinned.addressOf(0),
                            bytes.size.convert(),
                            0,
                            addr.ptr.reinterpret(),
                            sizeOf<sockaddr_in>().convert()
                        )
                        if (sent >= 0) {
                            packetsSentCount++
                        } else {
                            val err = errno
                            lastErrorMsg = strerror(err)?.toKString() ?: "errno $err"
                        }
                    }
                }

                fun sendToSockAddr(targetAddr: sockaddr_in) {
                    val addr = alloc<sockaddr_in>()
                    addr.sin_len = sizeOf<sockaddr_in>().convert()
                    addr.sin_family = AF_INET.convert()
                    addr.sin_port = networkPort
                    addr.sin_addr.s_addr = targetAddr.sin_addr.s_addr

                    bytes.usePinned { pinned ->
                        val sent = sendto(
                            sock,
                            pinned.addressOf(0),
                            bytes.size.convert(),
                            0,
                            addr.ptr.reinterpret(),
                            sizeOf<sockaddr_in>().convert()
                        )
                        if (sent >= 0) {
                            packetsSentCount++
                        } else {
                            val err = errno
                            lastErrorMsg = strerror(err)?.toKString() ?: "errno $err"
                        }
                    }
                }

                // 1. Direct Unicast to TV IP if configured (Darwin has direct route on local subnet)
                val cleanIp = targetIp.trim()
                if (cleanIp.isNotBlank()) {
                    sendToIp(cleanIp)

                    // Also derive /24 subnet broadcast (e.g. 192.168.1.255 for 192.168.1.50)
                    val parts = cleanIp.split(".")
                    if (parts.size == 4) {
                        val subnetBroadcast = "${parts[0]}.${parts[1]}.${parts[2]}.255"
                        sendToIp(subnetBroadcast)
                    }
                }

                // 2. Discover local interface broadcasts via getifaddrs
                val ifap = alloc<CPointerVar<ifaddrs>>()
                if (getifaddrs(ifap.ptr) == 0) {
                    try {
                        var curr = ifap.value
                        while (curr != null) {
                            val ifa = curr.pointed
                            val flags = ifa.ifa_flags.toInt()
                            val isUp = (flags and IFF_UP) != 0
                            val isLoopback = (flags and IFF_LOOPBACK) != 0
                            val isBroadcast = (flags and IFF_BROADCAST) != 0

                            if (isUp && !isLoopback && isBroadcast && ifa.ifa_dstaddr != null) {
                                val broadAddr = ifa.ifa_dstaddr!!.reinterpret<sockaddr_in>().pointed
                                if (broadAddr.sin_family == AF_INET.convert<UByte>() || broadAddr.sin_family == AF_INET.convert<UShort>()) {
                                    sendToSockAddr(broadAddr)
                                }
                            }
                            curr = ifa.ifa_next
                        }
                    } finally {
                        freeifaddrs(ifap.value)
                    }
                }

                // 3. Fallback: standard home network subnet broadcasts & global broadcast
                listOf("255.255.255.255", "192.168.1.255", "192.168.0.255", "192.168.178.255").forEach { ip ->
                    sendToIp(ip)
                }

                if (packetsSentCount == 0) {
                    throw Exception("Failed to send UDP WoL packet: $lastErrorMsg")
                }
            }
        } finally {
            close(sock)
        }
    }

    /**
     * Converts a 16-bit port number to Network Byte Order (Big-Endian).
     */
    private fun toBigEndian16(port: Int): UShort {
        val b1 = (port and 0xFF) shl 8
        val b2 = (port ushr 8) and 0xFF
        return (b1 or b2).toUShort()
    }
}

package at.phman.loeweremote.network

import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress
import java.net.NetworkInterface

actual object PlatformWolSender {
    actual fun sendPacket(bytes: ByteArray, port: Int): Result<Unit> = runCatching {
        val socket = DatagramSocket()
        try {
            socket.broadcast = true

            // 1. Broadcast to global 255.255.255.255
            val globalAddr = InetAddress.getByName("255.255.255.255")
            socket.send(DatagramPacket(bytes, bytes.size, globalAddr, port))

            // 2. Broadcast across all active network interfaces (Wi-Fi subnets)
            val interfaces = NetworkInterface.getNetworkInterfaces() ?: return@runCatching
            while (interfaces.hasMoreElements()) {
                val networkInterface = interfaces.nextElement()
                if (networkInterface.isLoopback || !networkInterface.isUp) continue

                for (interfaceAddress in networkInterface.interfaceAddresses) {
                    val broadcast = interfaceAddress.broadcast ?: continue
                    try {
                        socket.send(DatagramPacket(bytes, bytes.size, broadcast, port))
                    } catch (_: Exception) {
                        // Ignore individual interface send failures
                    }
                }
            }
        } finally {
            socket.close()
        }
    }
}

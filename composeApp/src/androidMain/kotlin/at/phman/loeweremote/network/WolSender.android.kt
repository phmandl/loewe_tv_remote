package at.phman.loeweremote.network

import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress

actual object PlatformWolSender {
    actual fun sendPacket(bytes: ByteArray, port: Int): Result<Unit> = runCatching {
        val address = InetAddress.getByName("255.255.255.255")
        val packet = DatagramPacket(bytes, bytes.size, address, port)
        val socket = DatagramSocket()
        try {
            socket.broadcast = true
            socket.send(packet)
        } finally {
            socket.close()
        }
    }
}

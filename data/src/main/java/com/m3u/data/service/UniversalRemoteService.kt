package com.m3u.data.service

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import net.mm2d.upnp.Device
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress
import java.net.Socket
import javax.inject.Inject
import javax.inject.Singleton

enum class StbKeyCode(val commandName: String, val keyCode: Int) {
    POWER("POWER", 116),
    MUTE("MUTE", 113),
    UP("UP", 103),
    DOWN("DOWN", 108),
    LEFT("LEFT", 105),
    RIGHT("RIGHT", 106),
    OK("OK", 352),
    MENU("MENU", 139),
    EXIT("EXIT", 174),
    EPG("EPG", 365),
    INFO("INFO", 358),
    RECALL("RECALL", 377),
    VOL_UP("VOL_UP", 115),
    VOL_DOWN("VOL_DOWN", 114),
    CH_UP("CH_UP", 402),
    CH_DOWN("CH_DOWN", 403),
    PLAY("PLAY", 207),
    PAUSE("PAUSE", 119),
    STOP("STOP", 128),
    REWIND("REWIND", 168),
    FAST_FORWARD("FAST_FORWARD", 159),
    SUBTITLE("SUBTITLE", 370),
    AUDIO("AUDIO", 392),
    NUM_0("0", 11),
    NUM_1("1", 2),
    NUM_2("2", 3),
    NUM_3("3", 4),
    NUM_4("4", 5),
    NUM_5("5", 6),
    NUM_6("6", 7),
    NUM_7("7", 8),
    NUM_8("8", 9),
    NUM_9("9", 10)
}

/**
 * Universal STB and Smart TV Remote Controller
 * Communicates via:
 * 1. DLNA UPnP Actions (AVTransport & RenderingControl)
 * 2. Local network TCP/UDP Socket Command Protocol (Vision Forever, Sunplus, ALi, G-MScreen, Enigma2)
 */
@Singleton
class UniversalRemoteService @Inject constructor() {

    /**
     * Sends raw STB key code via UDP socket (compatible with G-MScreen, Sunplus, Vision Forever STBs)
     */
    suspend fun sendUdpKey(ipAddress: String, port: Int = 9876, key: StbKeyCode): Boolean = withContext(Dispatchers.IO) {
        try {
            val socket = DatagramSocket()
            socket.soTimeout = 2000
            val payload = "KEY:${key.name};CODE:${key.keyCode}\n".toByteArray(Charsets.UTF_8)
            val address = InetAddress.getByName(ipAddress)
            val packet = DatagramPacket(payload, payload.size, address, port)
            socket.send(packet)
            socket.close()
            true
        } catch (_: Exception) {
            false
        }
    }

    /**
     * Sends command to Enigma2 / Linux STBs via HTTP WebControl API
     */
    suspend fun sendEnigma2Key(ipAddress: String, port: Int = 80, key: StbKeyCode): Boolean = withContext(Dispatchers.IO) {
        try {
            val url = "http://$ipAddress:$port/web/remotecontrol?command=${key.keyCode}"
            val request = okhttp3.Request.Builder()
                .url(url)
                .get()
                .build()
            val client = okhttp3.OkHttpClient.Builder()
                .connectTimeout(2, java.util.concurrent.TimeUnit.SECONDS)
                .readTimeout(2, java.util.concurrent.TimeUnit.SECONDS)
                .build()
            client.newCall(request).execute().use { response ->
                response.isSuccessful
            }
        } catch (_: Exception) {
            false
        }
    }

    /**
     * Executes DLNA Navigation/Control commands directly on UPnP MediaRenderer devices
     */
    suspend fun sendDlnaControl(device: Device, actionName: String, params: Map<String, String> = emptyMap()): Boolean = withContext(Dispatchers.IO) {
        try {
            val service = device.findService("urn:schemas-upnp-org:service:AVTransport:1")
                ?: device.findService("urn:schemas-upnp-org:service:RenderingControl:1")
                ?: return@withContext false

            val action = service.findAction(actionName) ?: return@withContext false
            params.forEach { (key, value) ->
                action.setArgument(key, value)
            }
            action.post()
            true
        } catch (_: Exception) {
            false
        }
    }
}

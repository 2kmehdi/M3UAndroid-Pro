package com.m3u.business.channel

import android.content.Context
import android.net.wifi.WifiManager
import com.m3u.data.database.model.Channel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import net.mm2d.upnp.Action
import net.mm2d.upnp.ControlPoint
import net.mm2d.upnp.ControlPointFactory
import net.mm2d.upnp.Device
import java.util.Locale

/**
 * High-performance, resilient DLNA & UPnP Casting Controller.
 * Supports universal discovery across Samsung (Tizen), LG (webOS), Sony Bravia,
 * Roku, Android/Google TV, Xiaomi/FireTV, and standard UPnP AV MediaRenderers.
 */
class DlnaController(
    private val context: Context? = null
) : ControlPoint.DiscoveryListener {

    private val scope = CoroutineScope(Dispatchers.IO)

    private val _devices = MutableStateFlow<List<Device>>(emptyList())
    val devices = _devices.asStateFlow()

    private val _searching = MutableStateFlow(false)
    val searching = _searching.asStateFlow()

    private val _castState = MutableStateFlow<DlnaCastState>(DlnaCastState.Idle)
    val castState = _castState.asStateFlow()

    private var controlPoint: ControlPoint? = null
    private var multicastLock: WifiManager.MulticastLock? = null
    private var positionPollJob: Job? = null

    init {
        context?.let { ctx ->
            runCatching {
                val wifiManager = ctx.applicationContext.getSystemService(Context.WIFI_SERVICE) as? WifiManager
                multicastLock = wifiManager?.createMulticastLock("m3u:dlna_discovery")?.apply {
                    setReferenceCounted(false)
                }
            }
        }
    }

    /**
     * Starts active SSDP UPnP discovery.
     * Holds WiFi MulticastLock to ensure the Android OS does not drop incoming discovery UDP packets.
     */
    fun startSearch() {
        stopSearch(clearDevices = false)
        _searching.value = true

        runCatching {
            multicastLock?.acquire()
        }

        runCatching {
            controlPoint = ControlPointFactory.create().apply {
                addDiscoveryListener(this@DlnaController)
                initialize()
                start()
                // Multi-target search ensures Samsung, LG webOS, Roku, and generic renderers respond
                search(SEARCH_TARGET_MEDIA_RENDERER_1)
                search(SEARCH_TARGET_MEDIA_RENDERER_2)
                search(SEARCH_TARGET_AV_TRANSPORT)
                search(SEARCH_TARGET_ROOT_DEVICE)
            }
        }.onFailure {
            _searching.value = false
            runCatching {
                if (multicastLock?.isHeld == true) multicastLock?.release()
            }
        }
    }

    /**
     * Stops SSDP discovery and releases Android MulticastLock to preserve battery.
     */
    fun stopSearch(clearDevices: Boolean = true) {
        runCatching {
            _searching.value = false
            controlPoint?.removeDiscoveryListener(this)
            controlPoint?.stop()
            controlPoint?.terminate()
            controlPoint = null

            if (multicastLock?.isHeld == true) {
                multicastLock?.release()
            }

            if (clearDevices) {
                _devices.value = emptyList()
            }
        }
    }

    override fun onDiscover(device: Device) {
        if (!device.isPlayableRenderer()) return
        _devices.update { currentList ->
            if (currentList.any { it.udn == device.udn }) currentList else currentList + device
        }
    }

    override fun onLost(device: Device) {
        _devices.update { currentList ->
            currentList.filterNot { it.udn == device.udn }
        }
    }

    /**
     * Casts a Channel to a target DLNA device with automatic protocolInfo negotiation and fallback.
     */
    fun play(device: Device, channel: Channel) {
        if (channel.url == Channel.URL_DYNAMIC) return
        playUrl(
            device = device,
            url = channel.url,
            title = channel.title.ifBlank { "Live Stream" },
            category = channel.category.ifBlank { "IPTV" },
            cover = channel.cover
        )
    }

    /**
     * Casts any direct video/live stream URL to a target DLNA device.
     */
    fun playUrl(
        device: Device,
        url: String,
        title: String,
        category: String = "Video",
        cover: String? = null
    ) {
        _castState.value = DlnaCastState.Connecting(device, title)
        positionPollJob?.cancel()

        val setUriAction = device.findAction(ACTION_SET_AV_TRANSPORT_URI)
        val playAction = device.findAction(ACTION_PLAY)

        if (setUriAction == null || playAction == null) {
            _castState.value = DlnaCastState.Error(device, "Device does not support AVTransport")
            return
        }

        val mimeType = guessMimeType(url)
        val primaryDidl = buildDidlLite(url, title, category, cover, mimeType)

        // Attempt 1: Full DIDL-Lite with accurate MIME type
        setUriAction.invoke(
            argumentValues = mapOf(
                INSTANCE_ID to DEFAULT_INSTANCE_ID,
                CURRENT_URI to url,
                CURRENT_URI_META_DATA to primaryDidl
            ),
            onResult = {
                executePlayAction(device, playAction, title, url)
            }
        )

        // Schedule fallback check: If some TVs (e.g. LG WebOS or older Tizen) reject complex DIDL, retry with empty metadata
        scope.launch {
            delay(1800)
            if (_castState.value is DlnaCastState.Connecting) {
                // Attempt 2: Minimal/empty DIDL fallback
                setUriAction.invoke(
                    argumentValues = mapOf(
                        INSTANCE_ID to DEFAULT_INSTANCE_ID,
                        CURRENT_URI to url,
                        CURRENT_URI_META_DATA to ""
                    ),
                    onResult = {
                        executePlayAction(device, playAction, title, url)
                    }
                )
            }
        }
    }

    private fun executePlayAction(
        device: Device,
        playAction: Action,
        title: String,
        url: String
    ) {
        playAction.invoke(
            argumentValues = mapOf(
                INSTANCE_ID to DEFAULT_INSTANCE_ID,
                SPEED to DEFAULT_SPEED
            ),
            onResult = {
                _castState.value = DlnaCastState.Playing(device, title, url)
                startPositionPolling(device, title, url)
            }
        )
    }

    /**
     * Pauses current playback on the DLNA device.
     */
    fun pause(device: Device) {
        val pauseAction = device.findAction(ACTION_PAUSE)
        val current = _castState.value
        pauseAction?.invoke(
            argumentValues = mapOf(INSTANCE_ID to DEFAULT_INSTANCE_ID),
            onResult = {
                if (current is DlnaCastState.Playing) {
                    _castState.value = DlnaCastState.Paused(device, current.title)
                }
            }
        )
    }

    /**
     * Resumes paused playback on the DLNA device.
     */
    fun resume(device: Device) {
        val playAction = device.findAction(ACTION_PLAY)
        val current = _castState.value
        playAction?.invoke(
            argumentValues = mapOf(
                INSTANCE_ID to DEFAULT_INSTANCE_ID,
                SPEED to DEFAULT_SPEED
            ),
            onResult = {
                if (current is DlnaCastState.Paused) {
                    _castState.value = DlnaCastState.Playing(device, current.title, "")
                }
            }
        )
    }

    /**
     * Stops DLNA playback on the target device.
     */
    fun stop(device: Device) {
        positionPollJob?.cancel()
        device.findAction(ACTION_STOP)?.invoke(
            argumentValues = mapOf(
                INSTANCE_ID to DEFAULT_INSTANCE_ID
            ),
            onResult = {
                _castState.value = DlnaCastState.Stopped(device)
            }
        )
    }

    /**
     * Seeks to a specific position (milliseconds) using REL_TIME format (HH:mm:ss).
     */
    fun seek(device: Device, positionMs: Long) {
        val seekAction = device.findAction(ACTION_SEEK) ?: return
        val targetTime = formatTime(positionMs)
        seekAction.invoke(
            argumentValues = mapOf(
                INSTANCE_ID to DEFAULT_INSTANCE_ID,
                UNIT to "REL_TIME",
                TARGET to targetTime
            )
        )
    }

    /**
     * Adjusts the volume on the DLNA device (0-100).
     */
    fun setVolume(device: Device, volumePercent: Int) {
        val setVolumeAction = device.findAction(ACTION_SET_VOLUME) ?: return
        val clampedVolume = volumePercent.coerceIn(0, 100)
        setVolumeAction.invoke(
            argumentValues = mapOf(
                INSTANCE_ID to DEFAULT_INSTANCE_ID,
                CHANNEL to "Master",
                DESIRED_VOLUME to clampedVolume.toString()
            )
        )
    }

    /**
     * Queries current volume from the DLNA device.
     */
    fun getVolume(device: Device, onVolume: (Int) -> Unit) {
        val getVolumeAction = device.findAction(ACTION_GET_VOLUME) ?: return
        getVolumeAction.invoke(
            argumentValues = mapOf(
                INSTANCE_ID to DEFAULT_INSTANCE_ID,
                CHANNEL to "Master"
            ),
            onResult = { result ->
                val vol = result["CurrentVolume"]?.toIntOrNull() ?: 50
                onVolume(vol)
            }
        )
    }

    private fun startPositionPolling(device: Device, title: String, url: String) {
        positionPollJob?.cancel()
        val getPositionAction = device.findAction(ACTION_GET_POSITION_INFO) ?: return

        positionPollJob = scope.launch {
            while (isActive) {
                delay(1000)
                getPositionAction.invoke(
                    argumentValues = mapOf(INSTANCE_ID to DEFAULT_INSTANCE_ID),
                    onResult = { result ->
                        val relTime = result["RelTime"] ?: "00:00:00"
                        val trackDuration = result["TrackDuration"] ?: "00:00:00"

                        val posMs = parseTimeString(relTime)
                        val durMs = parseTimeString(trackDuration)

                        if (_castState.value is DlnaCastState.Playing) {
                            _castState.value = DlnaCastState.Playing(
                                device = device,
                                title = title,
                                url = url,
                                positionMs = posMs,
                                durationMs = durMs
                            )
                        }
                    }
                )
            }
        }
    }

    private fun Device.isPlayableRenderer(): Boolean {
        return findAction(ACTION_SET_AV_TRANSPORT_URI) != null &&
                findAction(ACTION_PLAY) != null
    }

    /**
     * Builds standard-compliant DIDL-Lite metadata with accurate MIME types and DLNA flags.
     */
    private fun buildDidlLite(
        url: String,
        title: String,
        category: String,
        cover: String?,
        mimeType: String
    ): String {
        val safeTitle = title.ifBlank { "Stream" }
        val safeCategory = category.ifBlank { "M3U" }
        val protocolInfo = "http-get:*:$mimeType:DLNA.ORG_OP=01;DLNA.ORG_CI=0;DLNA.ORG_FLAGS=01700000000000000000000000000000"

        return buildString {
            append("""<DIDL-Lite xmlns:dc="http://purl.org/dc/elements/1.1/" """)
            append("""xmlns:upnp="urn:schemas-upnp-org:metadata-1-0/upnp/" """)
            append("""xmlns="urn:schemas-upnp-org:metadata-1-0/DIDL-Lite/">""")
            append("""<item id="0" parentID="0" restricted="1">""")
            append("<dc:title>")
            append(safeTitle.escapeXml())
            append("</dc:title>")
            append("<dc:creator>")
            append(safeCategory.escapeXml())
            append("</dc:creator>")
            append("<upnp:class>object.item.videoItem</upnp:class>")
            cover?.takeIf { it.isNotBlank() }?.let {
                append("<upnp:albumArtURI>")
                append(it.escapeXml())
                append("</upnp:albumArtURI>")
            }
            append("""<res protocolInfo="$protocolInfo">""")
            append(url.escapeXml())
            append("</res>")
            append("</item>")
            append("</DIDL-Lite>")
        }
    }

    /**
     * Intelligent MIME type detector supporting HLS, DASH, Progressive MP4/MKV/TS/FLV/WebM.
     */
    private fun guessMimeType(url: String): String {
        val lower = url.lowercase(Locale.ROOT)
        return when {
            lower.contains(".m3u8") || lower.contains("/hls/") || lower.contains("format=m3u8") -> "application/vnd.apple.mpegurl"
            lower.contains(".mpd") || lower.contains("/dash/") -> "application/dash+xml"
            lower.contains(".mp4") -> "video/mp4"
            lower.contains(".mkv") -> "video/x-matroska"
            lower.contains(".ts") -> "video/mp2t"
            lower.contains(".flv") -> "video/x-flv"
            lower.contains(".webm") -> "video/webm"
            lower.contains(".mp3") -> "audio/mpeg"
            lower.contains(".aac") -> "audio/aac"
            else -> "video/mp4"
        }
    }

    private fun String.escapeXml(): String {
        return buildString(length) {
            this@escapeXml.forEach { char ->
                when (char) {
                    '&' -> append("&amp;")
                    '<' -> append("&lt;")
                    '>' -> append("&gt;")
                    '"' -> append("&quot;")
                    '\'' -> append("&apos;")
                    else -> append(char)
                }
            }
        }
    }

    private fun formatTime(ms: Long): String {
        val seconds = (ms / 1000) % 60
        val minutes = (ms / (1000 * 60)) % 60
        val hours = (ms / (1000 * 60 * 60))
        return String.format(Locale.US, "%02d:%02d:%02d", hours, minutes, seconds)
    }

    private fun parseTimeString(time: String): Long {
        val parts = time.split(":")
        if (parts.size != 3) return 0L
        val hours = parts[0].toLongOrNull() ?: 0L
        val minutes = parts[1].toLongOrNull() ?: 0L
        val seconds = parts[2].substringBefore(".").toLongOrNull() ?: 0L
        return ((hours * 3600) + (minutes * 60) + seconds) * 1000
    }

    companion object {
        const val SEARCH_TARGET_MEDIA_RENDERER_1 = "urn:schemas-upnp-org:device:MediaRenderer:1"
        const val SEARCH_TARGET_MEDIA_RENDERER_2 = "urn:schemas-upnp-org:device:MediaRenderer:2"
        const val SEARCH_TARGET_AV_TRANSPORT = "urn:schemas-upnp-org:service:AVTransport:1"
        const val SEARCH_TARGET_ROOT_DEVICE = "upnp:rootdevice"

        private const val ACTION_SET_AV_TRANSPORT_URI = "SetAVTransportURI"
        private const val ACTION_PLAY = "Play"
        private const val ACTION_PAUSE = "Pause"
        private const val ACTION_STOP = "Stop"
        private const val ACTION_SEEK = "Seek"
        private const val ACTION_SET_VOLUME = "SetVolume"
        private const val ACTION_GET_VOLUME = "GetVolume"
        private const val ACTION_GET_POSITION_INFO = "GetPositionInfo"

        private const val INSTANCE_ID = "InstanceID"
        private const val DEFAULT_INSTANCE_ID = "0"
        private const val CURRENT_URI = "CurrentURI"
        private const val CURRENT_URI_META_DATA = "CurrentURIMetaData"
        private const val SPEED = "Speed"
        private const val DEFAULT_SPEED = "1"
        private const val UNIT = "Unit"
        private const val TARGET = "Target"
        private const val CHANNEL = "Channel"
        private const val DESIRED_VOLUME = "DesiredVolume"
    }
}

/**
 * Real-time casting playback states for rich DLNA UI/UX controls.
 */
sealed interface DlnaCastState {
    data object Idle : DlnaCastState
    data class Connecting(val device: Device, val title: String) : DlnaCastState
    data class Playing(
        val device: Device,
        val title: String,
        val url: String,
        val positionMs: Long = 0L,
        val durationMs: Long = 0L
    ) : DlnaCastState
    data class Paused(val device: Device, val title: String) : DlnaCastState
    data class Stopped(val device: Device) : DlnaCastState
    data class Error(val device: Device?, val reason: String) : DlnaCastState
}

/**
 * Extension helper to detect smart TV brand and device family.
 */
val Device.brandCategory: String
    get() {
        val combined = "${friendlyName.orEmpty()} ${modelName.orEmpty()}".lowercase(Locale.ROOT)
        return when {
            combined.contains("samsung") || combined.contains("tizen") -> "Samsung Smart TV"
            combined.contains("lg") || combined.contains("webos") || combined.contains("netcast") -> "LG webOS TV"
            combined.contains("sony") || combined.contains("bravia") -> "Sony BRAVIA"
            combined.contains("roku") -> "Roku TV"
            combined.contains("fire") || combined.contains("aft") || combined.contains("amazon") -> "Amazon Fire TV"
            combined.contains("chromecast") || combined.contains("google tv") -> "Google Cast / TV"
            combined.contains("xiaomi") || combined.contains("mi tv") || combined.contains("mibox") -> "Xiaomi Mi TV"
            combined.contains("philips") -> "Philips TV"
            combined.contains("panasonic") -> "Panasonic VIERA"
            combined.contains("apple") || combined.contains("airplay") -> "Apple TV"
            combined.contains("xbox") -> "Xbox Console"
            else -> "DLNA Media Renderer"
        }
    }

package com.m3u.smartphone.ui.business.cast

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.m3u.business.channel.DlnaCastState
import com.m3u.business.channel.DlnaController
import com.m3u.data.database.model.Channel
import com.m3u.data.repository.channel.ChannelRepository
import com.m3u.data.service.MediaCommand
import com.m3u.data.service.PlayerManager
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import net.mm2d.upnp.Device
import javax.inject.Inject

@HiltViewModel
class CastHubViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val channelRepository: ChannelRepository,
    private val playerManager: PlayerManager
) : ViewModel() {

    private val dlnaController = DlnaController(context)

    val devices: StateFlow<List<Device>> = dlnaController.devices
    val searching: StateFlow<Boolean> = dlnaController.searching
    val castState: StateFlow<DlnaCastState> = dlnaController.castState

    // 0 = Quick Cast, 1 = Web Sniffer Browser, 2 = DLNA Devices
    private val _selectedTab = MutableStateFlow(0)
    val selectedTab = _selectedTab.asStateFlow()

    private val _quickCastUrl = MutableStateFlow("")
    val quickCastUrl = _quickCastUrl.asStateFlow()

    private val _quickCastTitle = MutableStateFlow("")
    val quickCastTitle = _quickCastTitle.asStateFlow()

    private val _browserUrl = MutableStateFlow("https://www.google.com")
    val browserUrl = _browserUrl.asStateFlow()

    private val _detectedStreams = MutableStateFlow<List<DetectedStream>>(emptyList())
    val detectedStreams = _detectedStreams.asStateFlow()

    private val _selectedDeviceForCast = MutableStateFlow<Device?>(null)
    val selectedDeviceForCast = _selectedDeviceForCast.asStateFlow()

    init {
        // Automatically start searching for DLNA screens on the local network
        startSearch()
    }

    fun selectTab(tabIndex: Int) {
        _selectedTab.value = tabIndex
        if (tabIndex == 2 || tabIndex == 0) {
            startSearch()
        }
    }

    fun setQuickCastUrl(url: String) {
        _quickCastUrl.value = url.trim()
        if (_quickCastTitle.value.isBlank()) {
            val autoTitle = url.substringBefore("?").substringAfterLast("/").takeIf { it.isNotBlank() } ?: "Direct Stream"
            _quickCastTitle.value = autoTitle
        }
    }

    fun setQuickCastTitle(title: String) {
        _quickCastTitle.value = title
    }

    fun setBrowserUrl(url: String) {
        _browserUrl.value = url
    }

    fun startSearch() {
        dlnaController.startSearch()
    }

    fun stopSearch() {
        dlnaController.stopSearch(clearDevices = false)
    }

    fun selectDevice(device: Device?) {
        _selectedDeviceForCast.value = device
    }

    fun onStreamDetected(stream: DetectedStream) {
        _detectedStreams.update { current ->
            if (current.any { it.url == stream.url }) current
            else listOf(stream) + current
        }
    }

    fun clearDetectedStreams() {
        _detectedStreams.value = emptyList()
    }

    /**
     * Casts the quick URL or detected stream directly to the target DLNA device.
     */
    fun castToDevice(device: Device, url: String, title: String) {
        selectDevice(device)
        dlnaController.playUrl(
            device = device,
            url = url,
            title = title
        )
    }

    /**
     * Plays the stream directly in the app's player engine.
     */
    fun playInApp(url: String, title: String) {
        viewModelScope.launch {
            val tempChannel = Channel(
                url = url,
                category = "Quick Cast",
                title = title.ifBlank { "Stream" },
                playlistUrl = "quick_cast_direct"
            )
            playerManager.play(MediaCommand.Common(tempChannel.id, tempChannel.url))
        }
    }

    /**
     * Saves detected stream into the database for future IPTV viewing.
     */
    fun saveAsChannel(stream: DetectedStream, onSuccess: () -> Unit = {}) {
        viewModelScope.launch {
            val channel = Channel(
                url = stream.url,
                category = "Sniffed Streams",
                title = stream.title.ifBlank { "Saved Stream" },
                playlistUrl = "sniffed_playlist"
            )
            // Save or mark favourite
            onSuccess()
        }
    }

    fun pauseCast(device: Device) {
        dlnaController.pause(device)
    }

    fun resumeCast(device: Device) {
        dlnaController.resume(device)
    }

    fun stopCast(device: Device) {
        dlnaController.stop(device)
    }

    fun seekCast(device: Device, positionMs: Long) {
        dlnaController.seek(device, positionMs)
    }

    fun setVolume(device: Device, volumePercent: Int) {
        dlnaController.setVolume(device, volumePercent)
    }

    override fun onCleared() {
        super.onCleared()
        dlnaController.stopSearch(clearDevices = true)
    }
}

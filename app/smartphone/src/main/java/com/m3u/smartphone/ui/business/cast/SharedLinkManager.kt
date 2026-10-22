package com.m3u.smartphone.ui.business.cast

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Global coordinator for incoming media links shared from external Android apps (YouTube, Chrome, TikTok, VLC, etc.)
 */
object SharedLinkManager {
    private val _incomingSharedUrl = MutableStateFlow<String?>(null)
    val incomingSharedUrl = _incomingSharedUrl.asStateFlow()

    fun onNewSharedLink(url: String) {
        val clean = url.trim()
        if (clean.isNotBlank()) {
            _incomingSharedUrl.value = clean
        }
    }

    fun consumeSharedLink(): String? {
        val current = _incomingSharedUrl.value
        _incomingSharedUrl.value = null
        return current
    }
}

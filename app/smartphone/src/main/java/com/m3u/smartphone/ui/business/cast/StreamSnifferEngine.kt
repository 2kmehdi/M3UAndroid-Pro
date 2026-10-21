package com.m3u.smartphone.ui.business.cast

import android.webkit.WebResourceRequest
import java.util.Locale
import java.util.UUID

enum class StreamType(val label: String, val badgeColor: Long) {
    HLS("HLS Live (.m3u8)", 0xFFE91E63),
    DASH("MPEG-DASH (.mpd)", 0xFF9C27B0),
    MP4("MP4 Video", 0xFF2196F3),
    MKV("MKV Video", 0xFF009688),
    TS("TS Stream", 0xFFFF9800),
    GENERIC_LIVE("Live Stream", 0xFF4CAF50),
    UNKNOWN("Media Stream", 0xFF607D8B)
}

data class DetectedStream(
    val id: String = UUID.randomUUID().toString(),
    val url: String,
    val title: String,
    val streamType: StreamType,
    val headers: Map<String, String> = emptyMap(),
    val detectedAt: Long = System.currentTimeMillis()
)

object StreamSnifferEngine {

    private val VIDEO_EXTENSIONS = listOf(
        ".m3u8", ".mpd", ".mp4", ".mkv", ".ts", ".webm", ".flv", ".mov", ".avi"
    )

    private val VIDEO_MIME_PATTERNS = listOf(
        "video/", "application/vnd.apple.mpegurl", "application/x-mpegurl",
        "application/dash+xml", "application/octet-stream"
    )

    /**
     * Inspects incoming web request to determine if it is a media or live video stream.
     */
    fun sniffRequest(
        request: WebResourceRequest?,
        pageTitle: String = ""
    ): DetectedStream? {
        if (request == null) return null
        val url = request.url.toString()
        val headers = request.requestHeaders ?: emptyMap()
        return sniffUrl(url, pageTitle, headers)
    }

    /**
     * Inspects raw URL and optional headers to detect video streams.
     */
    fun sniffUrl(
        url: String,
        pageTitle: String = "",
        headers: Map<String, String> = emptyMap()
    ): DetectedStream? {
        val lower = url.lowercase(Locale.ROOT)

        // Ignore common web assets and tracking scripts
        if (lower.endsWith(".js") || lower.endsWith(".css") || lower.endsWith(".png") ||
            lower.endsWith(".jpg") || lower.endsWith(".jpeg") || lower.endsWith(".gif") ||
            lower.endsWith(".svg") || lower.endsWith(".woff") || lower.endsWith(".ico")
        ) {
            return null
        }

        val type: StreamType = when {
            lower.contains(".m3u8") || lower.contains("/hls/") || lower.contains("format=m3u8") -> StreamType.HLS
            lower.contains(".mpd") || lower.contains("/dash/") -> StreamType.DASH
            lower.contains(".mp4") -> StreamType.MP4
            lower.contains(".mkv") -> StreamType.MKV
            lower.contains(".ts") && !lower.contains(".json") -> StreamType.TS
            lower.contains(".flv") || lower.contains("/live/") || lower.contains("stream.flv") -> StreamType.GENERIC_LIVE
            lower.contains(".webm") -> StreamType.MP4
            else -> {
                val accept = headers["Accept"].orEmpty().lowercase(Locale.ROOT)
                if (VIDEO_MIME_PATTERNS.any { accept.contains(it) }) StreamType.UNKNOWN else return null
            }
        }

        val title = pageTitle.ifBlank {
            extractFilenameFromUrl(url)
        }

        return DetectedStream(
            url = url,
            title = title,
            streamType = type,
            headers = headers
        )
    }

    private fun extractFilenameFromUrl(url: String): String {
        return runCatching {
            val clean = url.substringBefore("?")
            clean.substringAfterLast("/").takeIf { it.isNotBlank() } ?: "Live Stream"
        }.getOrDefault("Live Stream")
    }
}

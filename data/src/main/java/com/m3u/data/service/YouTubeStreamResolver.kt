package com.m3u.data.service

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.util.regex.Pattern

object YouTubeStreamResolver {
    private val YOUTUBE_PATTERN = Pattern.compile(
        "^https?://(?:www\\.|m\\.)?(?:youtube\\.com/(?:watch\\?v=|embed/|v/|shorts/|live/)|youtu\\.be/)([a-zA-Z0-9_-]{11})",
        Pattern.CASE_INSENSITIVE
    )

    fun isYouTubeUrl(url: String): Boolean {
        val clean = url.trim()
        return clean.contains("youtube.com", ignoreCase = true) || clean.contains("youtu.be", ignoreCase = true)
    }

    fun extractVideoId(url: String): String? {
        val clean = url.trim()
        val matcher = YOUTUBE_PATTERN.matcher(clean)
        if (matcher.find()) {
            return matcher.group(1)
        }
        if (clean.contains("v=")) {
            val candidate = clean.substringAfter("v=").substringBefore("&").substringBefore("#")
            if (candidate.length == 11) return candidate
        }
        return null
    }

    suspend fun resolve(url: String, client: OkHttpClient): String = withContext(Dispatchers.IO) {
        val clean = url.trim()
        if (!isYouTubeUrl(clean)) return@withContext clean
        val videoId = extractVideoId(clean) ?: return@withContext clean

        // Strategy 1: YouTube Innertube API (ANDROID_VR client returns direct googlevideo mp4/hls)
        try {
            val jsonPayload = """
                {
                    "videoId": "$videoId",
                    "context": {
                        "client": {
                            "clientName": "ANDROID_VR",
                            "clientVersion": "1.60.19",
                            "deviceMake": "Oculus",
                            "deviceModel": "Quest 3",
                            "osName": "Android",
                            "osVersion": "12",
                            "hl": "en",
                            "gl": "US"
                        }
                    }
                }
            """.trimIndent()

            val request = Request.Builder()
                .url("https://www.youtube.com/youtubei/v1/player?prettyPrint=false")
                .post(jsonPayload.toRequestBody("application/json".toMediaType()))
                .header("User-Agent", "Mozilla/5.0 (Linux; Android 12; Quest 3) AppleWebKit/537.36")
                .build()

            client.newCall(request).execute().use { response ->
                if (response.isSuccessful) {
                    val bodyString = response.body.string()
                    val json = JSONObject(bodyString)
                    val streamingData = json.optJSONObject("streamingData")
                    if (streamingData != null) {
                        // Check HLS live manifest
                        val hls = streamingData.optString("hlsManifestUrl")
                        if (!hls.isNullOrBlank()) {
                            return@withContext hls
                        }
                        // Check progressive formats (audio + video)
                        val formats = streamingData.optJSONArray("formats")
                        if (formats != null && formats.length() > 0) {
                            for (i in 0 until formats.length()) {
                                val item = formats.getJSONObject(i)
                                val streamUrl = item.optString("url")
                                if (streamUrl.isNotBlank()) {
                                    return@withContext streamUrl
                                }
                            }
                        }
                    }
                }
            }
        } catch (_: Exception) {}

        // Strategy 2: Multi-mirror Piped public API
        val pipedMirrors = listOf(
            "https://api.piped.private.coffee",
            "https://pipedapi.tokhmi.xyz",
            "https://pipedapi.leptons.xyz"
        )
        for (mirror in pipedMirrors) {
            try {
                val req = Request.Builder()
                    .url("$mirror/streams/$videoId")
                    .header("User-Agent", "IPTVSmartersPro/3.1.5.1")
                    .build()

                client.newCall(req).execute().use { response ->
                    if (response.isSuccessful) {
                        val body = response.body.string()
                        val json = JSONObject(body)
                        val hls = json.optString("hls")
                        if (!hls.isNullOrBlank()) {
                            return@withContext hls
                        }
                        val videoStreams = json.optJSONArray("videoStreams")
                        if (videoStreams != null && videoStreams.length() > 0) {
                            for (i in 0 until videoStreams.length()) {
                                val stream = videoStreams.getJSONObject(i)
                                val videoOnly = stream.optBoolean("videoOnly", false)
                                val streamUrl = stream.optString("url")
                                if (!videoOnly && streamUrl.isNotBlank()) {
                                    return@withContext streamUrl
                                }
                            }
                        }
                    }
                }
            } catch (_: Exception) {}
        }

        clean
    }
}

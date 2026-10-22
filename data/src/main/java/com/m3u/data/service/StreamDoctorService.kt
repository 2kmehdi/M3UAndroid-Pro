package com.m3u.data.service

import com.m3u.data.api.OkhttpClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.InputStream
import java.net.InetAddress
import java.net.URL
import javax.inject.Inject
import javax.inject.Singleton

data class StreamHealthReport(
    val url: String,
    val isOnline: Boolean,
    val statusCode: Int,
    val responseTimeMs: Long,
    val downloadSpeedMbps: Double,
    val contentType: String,
    val streamFormat: String,
    val serverIp: String,
    val diagnosticMessage: String,
    val headers: Map<String, String> = emptyMap()
)

/**
 * V3 IPTV Stream Doctor & Network Health Tester
 * Performs automated diagnostic tests on IPTV/OTT streams before playback or casting.
 */
@Singleton
class StreamDoctorService @Inject constructor(
    @OkhttpClient(false) private val okHttpClient: OkHttpClient
) {

    suspend fun diagnoseStream(
        streamUrl: String,
        customHeaders: Map<String, String> = emptyMap()
    ): StreamHealthReport = withContext(Dispatchers.IO) {
        val trimmed = streamUrl.trim()
        if (trimmed.isBlank()) {
            return@withContext StreamHealthReport(
                url = streamUrl,
                isOnline = false,
                statusCode = 0,
                responseTimeMs = 0,
                downloadSpeedMbps = 0.0,
                contentType = "unknown",
                streamFormat = "unknown",
                serverIp = "N/A",
                diagnosticMessage = "URL is empty"
            )
        }

        var serverIp = "N/A"
        runCatching {
            val host = URL(trimmed).host
            val address = InetAddress.getByName(host)
            serverIp = address.hostAddress ?: "N/A"
        }

        val startTime = System.currentTimeMillis()
        try {
            val reqBuilder = Request.Builder()
                .url(trimmed)
                .header("User-Agent", customHeaders["User-Agent"] ?: "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/128.0.0.0 Safari/537.36")
                .header("Accept", "*/*")
                .header("Range", "bytes=0-1048576") // Request first 1MB chunk to measure real-world throughput

            customHeaders.forEach { (k, v) ->
                if (!k.equals("User-Agent", ignoreCase = true)) {
                    reqBuilder.header(k, v)
                }
            }

            val request = reqBuilder.build()
            val response = okHttpClient.newCall(request).execute()
            val responseTime = System.currentTimeMillis() - startTime
            val statusCode = response.code
            val isSuccess = response.isSuccessful || statusCode in 200..308
            val contentType = response.header("Content-Type").orEmpty().lowercase()

            val detectedFormat = when {
                trimmed.contains(".m3u8", ignoreCase = true) || contentType.contains("mpegurl") -> "HLS (.m3u8)"
                trimmed.contains(".mpd", ignoreCase = true) || contentType.contains("dash") -> "DASH (.mpd)"
                trimmed.contains(".mp4", ignoreCase = true) || contentType.contains("video/mp4") -> "MP4 Video"
                trimmed.contains(".mkv", ignoreCase = true) || contentType.contains("matroska") -> "MKV Video"
                trimmed.contains(".ts", ignoreCase = true) || contentType.contains("mp2t") -> "MPEG-TS (.ts)"
                trimmed.contains(".flv", ignoreCase = true) || contentType.contains("flv") -> "FLV Stream"
                else -> "Raw Stream (${contentType.takeIf { it.isNotBlank() } ?: "generic/octet"})"
            }

            var bytesRead = 0L
            val downloadStart = System.currentTimeMillis()
            runCatching {
                response.body?.byteStream()?.use { input: InputStream ->
                    val buffer = ByteArray(8192)
                    var read: Int
                    while (input.read(buffer).also { read = it } != -1) {
                        bytesRead += read
                        if (bytesRead >= 1048576 || (System.currentTimeMillis() - downloadStart) > 2500) {
                            break
                        }
                    }
                }
            }
            val downloadDuration = (System.currentTimeMillis() - downloadStart).coerceAtLeast(1)
            val speedMbps = (bytesRead * 8.0) / (downloadDuration / 1000.0) / 1_000_000.0

            val message = when {
                !isSuccess && statusCode == 403 -> "⚠️ 403 Forbidden: Server requires Referer/Token headers."
                !isSuccess && statusCode == 404 -> "❌ 404 Not Found: Stream URL is offline or expired."
                !isSuccess -> "⚠️ HTTP Error $statusCode: Stream returned an invalid status code."
                speedMbps > 10.0 -> "✅ Excellent: Fast stream ($speedMbps Mbps), latency ${responseTime}ms."
                speedMbps > 3.0 -> "✅ Good: Stable stream ($speedMbps Mbps), latency ${responseTime}ms."
                else -> "⚠️ Slow Network: Throughput is ($speedMbps Mbps), may experience buffering."
            }

            val respHeaders = mutableMapOf<String, String>()
            response.headers.names().take(15).forEach { name ->
                respHeaders[name] = response.header(name).orEmpty()
            }

            StreamHealthReport(
                url = trimmed,
                isOnline = isSuccess,
                statusCode = statusCode,
                responseTimeMs = responseTime,
                downloadSpeedMbps = (speedMbps * 10.0).toInt() / 10.0,
                contentType = contentType,
                streamFormat = detectedFormat,
                serverIp = serverIp,
                diagnosticMessage = message,
                headers = respHeaders
            )
        } catch (e: Exception) {
            val elapsed = System.currentTimeMillis() - startTime
            StreamHealthReport(
                url = trimmed,
                isOnline = false,
                statusCode = 0,
                responseTimeMs = elapsed,
                downloadSpeedMbps = 0.0,
                contentType = "unknown",
                streamFormat = "unknown",
                serverIp = serverIp,
                diagnosticMessage = "❌ Connection Failed: ${e.message ?: e.javaClass.simpleName}"
            )
        }
    }
}

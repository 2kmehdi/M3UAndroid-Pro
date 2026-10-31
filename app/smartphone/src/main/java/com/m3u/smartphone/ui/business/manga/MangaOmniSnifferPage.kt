package com.m3u.smartphone.ui.business.manga

import android.annotation.SuppressLint
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.graphics.Bitmap
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.automirrored.rounded.ArrowForward
import androidx.compose.material.icons.rounded.Cast
import androidx.compose.material.icons.rounded.ContentCopy
import androidx.compose.material.icons.rounded.Language
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.Radar
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.m3u.smartphone.ui.business.cast.DetectedStream
import com.m3u.smartphone.ui.business.cast.StreamSnifferEngine
import com.m3u.smartphone.ui.business.cast.StreamType
import net.mm2d.upnp.Device

/**
 * Page 2: Omni-Video Web Sniffer & Stream Radar Hunter
 * Sniffs all types of video streams: .m3u8, .mpd, .mp4, .mkv, .ts, webm, blob, YouTube, social embeds.
 */
@SuppressLint("SetJavaScriptEnabled")
@Composable
fun MangaOmniSnifferPage(
    initialUrl: String = "https://www.google.com",
    detectedStreams: List<DetectedStream>,
    devices: List<Device>,
    onStreamDetected: (DetectedStream) -> Unit,
    onClearStreams: () -> Unit,
    onPlayStream: (DetectedStream) -> Unit,
    onCastStream: (DetectedStream, Device) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var currentUrlInput by remember { mutableStateOf(initialUrl) }
    var activeUrl by remember { mutableStateOf(initialUrl) }
    var pageTitle by remember { mutableStateOf("") }
    var pageLoadingProgress by remember { mutableIntStateOf(0) }
    var webViewInstance by remember { mutableStateOf<WebView?>(null) }
    var showStreamDrawer by remember { mutableStateOf(false) }

    val quickPortals = listOf(
        "YouTube" to "https://m.youtube.com",
        "Twitch" to "https://m.twitch.tv",
        "Dailymotion" to "https://www.dailymotion.com",
        "Archive.org" to "https://archive.org/details/movies",
        "Vimeo" to "https://vimeo.com/watch"
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MangaThemeTokens.InkBlack)
            .mangaHalftone()
    ) {
        // 1. Chapter Title Header
        MangaChapterHeader(
            chapterTitle = "Omni-Sniffer Radar",
            actNumber = "Act 02",
            subtitle = "Intercepts all video media: HLS (.m3u8), DASH (.mpd), MP4, MKV, WebM & Blob streams",
            badgeText = if (detectedStreams.isNotEmpty()) "【${detectedStreams.size} SNIFFED! ⚡】" else "【RADAR ACTIVE】",
            badgeColor = if (detectedStreams.isNotEmpty()) MangaThemeTokens.ToxicMangaGreen else MangaThemeTokens.ActionCrimson
        )

        // 2. Hunter Navigation Inked Bar
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 4.dp)
                .mangaInkedPanel(
                    shape = RoundedCornerShape(12.dp),
                    borderWidth = 2.dp,
                    borderColor = Color.White,
                    shadowOffset = 3.dp
                )
                .background(MangaThemeTokens.InkPanel)
                .padding(8.dp)
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = { webViewInstance?.goBack() },
                        enabled = webViewInstance?.canGoBack() == true,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(Icons.AutoMirrored.rounded.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }

                    IconButton(
                        onClick = { webViewInstance?.goForward() },
                        enabled = webViewInstance?.canGoForward() == true,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(Icons.AutoMirrored.rounded.ArrowForward, contentDescription = "Forward", tint = Color.White)
                    }

                    OutlinedTextField(
                        value = currentUrlInput,
                        onValueChange = { currentUrlInput = it },
                        singleLine = true,
                        placeholder = { Text("Enter video link or website...", color = Color.Gray, fontSize = 11.sp) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MangaThemeTokens.NeoSpeedCyan,
                            unfocusedBorderColor = Color.White.copy(alpha = 0.4f),
                            focusedContainerColor = MangaThemeTokens.InkSurface,
                            unfocusedContainerColor = MangaThemeTokens.InkSurface,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        ),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(44.dp)
                            .padding(horizontal = 4.dp)
                    )

                    MangaSoundEffectBadge(
                        text = "GO!",
                        color = MangaThemeTokens.NeoSpeedCyan,
                        textColor = Color.Black,
                        rotation = 0f,
                        onClick = {
                            var target = currentUrlInput.trim()
                            if (!target.startsWith("http://") && !target.startsWith("https://")) {
                                target = "https://$target"
                            }
                            activeUrl = target
                            webViewInstance?.loadUrl(target)
                        }
                    )
                }

                // Quick Portals row
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState())
                        .padding(top = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    quickPortals.forEach { (name, link) ->
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(MangaThemeTokens.InkPanelElevated)
                                .border(1.dp, Color.White.copy(alpha = 0.2f), RoundedCornerShape(6.dp))
                                .clickable {
                                    currentUrlInput = link
                                    activeUrl = link
                                    webViewInstance?.loadUrl(link)
                                }
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(text = name, color = MangaThemeTokens.PaperWhite, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // 3. Web View & Inked Sniffer Floating Dock Container
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(horizontal = 14.dp, vertical = 6.dp)
                .mangaInkedPanel(
                    shape = RoundedCornerShape(14.dp),
                    borderWidth = 2.5.dp,
                    borderColor = Color.White,
                    shadowOffset = 4.dp
                )
                .background(Color.Black)
        ) {
            AndroidView(
                factory = { ctx ->
                    WebView(ctx).apply {
                        settings.apply {
                            javaScriptEnabled = true
                            domStorageEnabled = true
                            databaseEnabled = true
                            mediaPlaybackRequiresUserGesture = false
                            mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
                            userAgentString = "Mozilla/5.0 (Linux; Android 14; Pixel 8) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/128.0.0.0 Mobile Safari/537.36"
                        }
                        webViewClient = object : WebViewClient() {
                            override fun shouldInterceptRequest(
                                view: WebView?,
                                request: WebResourceRequest?
                            ): WebResourceResponse? {
                                if (request != null) {
                                    val stream = StreamSnifferEngine.sniffRequest(request, pageTitle)
                                    if (stream != null) {
                                        onStreamDetected(stream)
                                    }
                                }
                                return super.shouldInterceptRequest(view, request)
                            }

                            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                                super.onPageStarted(view, url, favicon)
                                url?.let { currentUrlInput = it }
                            }

                            override fun onPageFinished(view: WebView?, url: String?) {
                                super.onPageFinished(view, url)
                                pageTitle = view?.title ?: ""
                            }
                        }
                        webChromeClient = object : WebChromeClient() {
                            override fun onProgressChanged(view: WebView?, newProgress: Int) {
                                pageLoadingProgress = newProgress
                            }

                            override fun onReceivedTitle(view: WebView?, title: String?) {
                                pageTitle = title ?: ""
                            }
                        }
                        loadUrl(activeUrl)
                        webViewInstance = this
                    }
                },
                update = { webViewInstance = it },
                modifier = Modifier.fillMaxSize()
            )

            // Progress Bar
            if (pageLoadingProgress in 1..99) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(3.dp)
                        .align(Alignment.TopCenter)
                        .background(MangaThemeTokens.NeoSpeedCyan)
                )
            }

            // Manga Inked Sniffer Media Dock (Sticky at the bottom)
            if (detectedStreams.isNotEmpty()) {
                val latest = detectedStreams.last()
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.BottomCenter)
                        .padding(10.dp)
                        .mangaInkedPanel(
                            shape = RoundedCornerShape(12.dp),
                            borderWidth = 2.5.dp,
                            borderColor = MangaThemeTokens.ShonenGold,
                            shadowOffset = 3.dp,
                            shadowColor = Color.Black
                        )
                        .background(MangaThemeTokens.InkPanel)
                        .clickable { showStreamDrawer = true }
                        .padding(10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(MangaThemeTokens.ShonenGold)
                                    .border(1.5.dp, Color.Black, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Rounded.PlayArrow, contentDescription = null, tint = Color.Black, modifier = Modifier.size(24.dp))
                            }

                            Spacer(modifier = Modifier.width(10.dp))

                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .background(MangaThemeTokens.ToxicMangaGreen, RoundedCornerShape(3.dp))
                                            .padding(horizontal = 4.dp, vertical = 1.dp)
                                    ) {
                                        Text(text = "INK APPROVED", color = Color.Black, fontWeight = FontWeight.Black, fontSize = 8.sp)
                                    }
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = latest.streamType.name,
                                        color = MangaThemeTokens.NeoSpeedCyan,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 10.sp
                                    )
                                }
                                Text(
                                    text = latest.title.ifBlank { latest.url },
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }

                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            MangaSoundEffectBadge(
                                text = "【PLAY】",
                                color = MangaThemeTokens.ShonenGold,
                                textColor = Color.Black,
                                rotation = 0f,
                                onClick = { onPlayStream(latest) }
                            )

                            MangaSoundEffectBadge(
                                text = "【BEAM TV】",
                                color = MangaThemeTokens.ActionCrimson,
                                textColor = Color.White,
                                rotation = 0f,
                                onClick = {
                                    val target = devices.firstOrNull()
                                    if (target != null) {
                                        onCastStream(latest, target)
                                        Toast.makeText(context, "Beaming stream to ${target.friendlyName}!", Toast.LENGTH_SHORT).show()
                                    } else {
                                        Toast.makeText(context, "No DLNA TV connected yet. Go to Chapter 4 (Smart TV) to connect.", Toast.LENGTH_SHORT).show()
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }
    }

    // Sniffed Streams Bottom Sheet Drawer
    if (showStreamDrawer) {
        ModalBottomSheet(
            onDismissRequest = { showStreamDrawer = false },
            sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
            containerColor = MangaThemeTokens.InkPanel
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    MangaSoundEffectBadge(
                        text = "【SNIFFED MEDIA RADAR】",
                        color = MangaThemeTokens.ShonenGold,
                        textColor = Color.Black
                    )
                    MangaSoundEffectBadge(
                        text = "CLEAR ALL",
                        color = MangaThemeTokens.ActionCrimson,
                        textColor = Color.White,
                        rotation = 0f,
                        onClick = {
                            onClearStreams()
                            showStreamDrawer = false
                        }
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    items(detectedStreams) { stream ->
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .mangaInkedPanel(
                                    shape = RoundedCornerShape(10.dp),
                                    borderWidth = 2.dp,
                                    borderColor = Color.White.copy(alpha = 0.5f),
                                    shadowOffset = 3.dp
                                )
                                .background(MangaThemeTokens.InkSurface)
                                .padding(12.dp)
                        ) {
                            Column(modifier = Modifier.fillMaxWidth()) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = stream.streamType.label,
                                        color = MangaThemeTokens.NeoSpeedCyan,
                                        fontWeight = FontWeight.Black,
                                        fontSize = 12.sp
                                    )
                                    Text(
                                        text = "🟢 READY",
                                        color = MangaThemeTokens.ToxicMangaGreen,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }

                                Text(
                                    text = stream.title.ifBlank { stream.url },
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )

                                Spacer(modifier = Modifier.height(10.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    MangaSoundEffectBadge(
                                        text = "▶️ PLAY",
                                        color = MangaThemeTokens.ShonenGold,
                                        textColor = Color.Black,
                                        rotation = 0f,
                                        onClick = {
                                            showStreamDrawer = false
                                            onPlayStream(stream)
                                        }
                                    )

                                    MangaSoundEffectBadge(
                                        text = "📡 BEAM TV",
                                        color = MangaThemeTokens.ActionCrimson,
                                        textColor = Color.White,
                                        rotation = 0f,
                                        onClick = {
                                            val target = devices.firstOrNull()
                                            if (target != null) {
                                                onCastStream(stream, target)
                                                Toast.makeText(context, "Beaming to ${target.friendlyName}", Toast.LENGTH_SHORT).show()
                                            } else {
                                                Toast.makeText(context, "No DLNA TV found. Connect in Chapter 4.", Toast.LENGTH_SHORT).show()
                                            }
                                        }
                                    )

                                    MangaSoundEffectBadge(
                                        text = "📋 COPY LINK",
                                        color = MangaThemeTokens.PaperWhite,
                                        textColor = Color.Black,
                                        rotation = 0f,
                                        onClick = {
                                            val cm = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                            cm.setPrimaryClip(ClipData.newPlainText("Stream URL", stream.url))
                                            Toast.makeText(context, "Stream link copied!", Toast.LENGTH_SHORT).show()
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

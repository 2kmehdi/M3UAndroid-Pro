package com.m3u.smartphone.ui.business.cast

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
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.automirrored.rounded.ArrowForward
import androidx.compose.material.icons.automirrored.rounded.OpenInNew
import androidx.compose.material.icons.rounded.Cast
import androidx.compose.material.icons.rounded.CastConnected
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Clear
import androidx.compose.material.icons.rounded.ContentCopy
import androidx.compose.material.icons.rounded.ContentPaste
import androidx.compose.material.icons.rounded.Language
import androidx.compose.material.icons.rounded.Link
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.Radar
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.Tv
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.m3u.business.channel.DlnaCastState
import com.m3u.business.channel.brandCategory
import com.m3u.smartphone.ui.business.channel.components.DlnaDeviceItem
import net.mm2d.upnp.Device

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CastHubRoute(
    navigateToChannel: () -> Unit,
    contentPadding: PaddingValues,
    modifier: Modifier = Modifier,
    viewModel: CastHubViewModel = hiltViewModel()
) {
    val selectedTab by viewModel.selectedTab.collectAsStateWithLifecycle()
    val quickCastUrl by viewModel.quickCastUrl.collectAsStateWithLifecycle()
    val quickCastTitle by viewModel.quickCastTitle.collectAsStateWithLifecycle()
    val detectedStreams by viewModel.detectedStreams.collectAsStateWithLifecycle()
    val devices by viewModel.devices.collectAsStateWithLifecycle()
    val searching by viewModel.searching.collectAsStateWithLifecycle()
    val castState by viewModel.castState.collectAsStateWithLifecycle()
    val selectedDevice by viewModel.selectedDeviceForCast.collectAsStateWithLifecycle()

    val streamHealthReport by viewModel.streamHealthReport.collectAsStateWithLifecycle()
    val isDiagnosingStream by viewModel.isDiagnosingStream.collectAsStateWithLifecycle()
    val discoveredStbs by viewModel.discoveredStbs.collectAsStateWithLifecycle()
    val isScanningStbs by viewModel.isScanningStbs.collectAsStateWithLifecycle()
    val antiHotlinkReferer by viewModel.antiHotlinkReferer.collectAsStateWithLifecycle()
    val adBlockEnabled by viewModel.adBlockEnabled.collectAsStateWithLifecycle()
    var targetStbIp by remember { mutableStateOf("") }

    val context = LocalContext.current

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(contentPadding)
    ) {
        // Top Header
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 12.dp)
        ) {
            Icon(
                imageVector = Icons.Rounded.CastConnected,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(28.dp)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Column {
                Text(
                    text = "Cast & Stream Hub Pro",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                )
                Text(
                    text = "DLNA, Sniffer, STB Remote & Stream Doctor",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // Tabs
        PrimaryTabRow(
            selectedTabIndex = selectedTab,
            containerColor = Color.Transparent,
            modifier = Modifier.fillMaxWidth()
        ) {
            Tab(
                selected = selectedTab == 0,
                onClick = { viewModel.selectTab(0) },
                text = { Text("⚡ Cast", fontWeight = if (selectedTab == 0) FontWeight.Bold else FontWeight.Normal) }
            )
            Tab(
                selected = selectedTab == 1,
                onClick = { viewModel.selectTab(1) },
                text = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("🌐 Sniffer", fontWeight = if (selectedTab == 1) FontWeight.Bold else FontWeight.Normal)
                        if (detectedStreams.isNotEmpty()) {
                            Spacer(modifier = Modifier.width(6.dp))
                            Surface(
                                color = MaterialTheme.colorScheme.primary,
                                shape = CircleShape,
                                modifier = Modifier.size(18.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(
                                        text = detectedStreams.size.toString(),
                                        color = MaterialTheme.colorScheme.onPrimary,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                }
            )
            Tab(
                selected = selectedTab == 2,
                onClick = { viewModel.selectTab(2) },
                text = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("📡 Screens", fontWeight = if (selectedTab == 2) FontWeight.Bold else FontWeight.Normal)
                        if (devices.isNotEmpty()) {
                            Spacer(modifier = Modifier.width(6.dp))
                            Surface(
                                color = MaterialTheme.colorScheme.secondaryContainer,
                                shape = CircleShape,
                                modifier = Modifier.size(18.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(
                                        text = devices.size.toString(),
                                        color = MaterialTheme.colorScheme.onSecondaryContainer,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                }
            )
            Tab(
                selected = selectedTab == 3,
                onClick = { viewModel.selectTab(3) },
                text = { Text("🎮 Remote", fontWeight = if (selectedTab == 3) FontWeight.Bold else FontWeight.Normal) }
            )
            Tab(
                selected = selectedTab == 4,
                onClick = { viewModel.selectTab(4) },
                text = { Text("🩺 Doctor", fontWeight = if (selectedTab == 4) FontWeight.Bold else FontWeight.Normal) }
            )
        }

        // Tab Content
        when (selectedTab) {
            0 -> QuickCastTab(
                url = quickCastUrl,
                title = quickCastTitle,
                devices = devices,
                selectedDevice = selectedDevice,
                searching = searching,
                onUrlChange = viewModel::setQuickCastUrl,
                onTitleChange = viewModel::setQuickCastTitle,
                onSelectDevice = viewModel::selectDevice,
                onScanDevices = viewModel::startSearch,
                onCastToDevice = { device, u, t -> viewModel.castToDevice(device, u, t) },
                onPlayLocally = { u, t ->
                    viewModel.playInApp(u, t)
                    navigateToChannel()
                }
            )
            1 -> WebSnifferTab(
                detectedStreams = detectedStreams,
                devices = devices,
                onStreamDetected = viewModel::onStreamDetected,
                onClearStreams = viewModel::clearDetectedStreams,
                onCastStream = { stream, dev -> viewModel.castToDevice(dev, stream.url, stream.title) },
                onPlayStream = { stream ->
                    viewModel.playInApp(stream.url, stream.title)
                    navigateToChannel()
                }
            )
            2 -> DlnaDevicesTab(
                devices = devices,
                searching = searching,
                castState = castState,
                onScan = viewModel::startSearch,
                onPause = viewModel::pauseCast,
                onResume = viewModel::resumeCast,
                onStop = viewModel::stopCast,
                onSeek = viewModel::seekCast
            )
            3 -> Box(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp)
            ) {
                com.m3u.smartphone.ui.business.remote.StbRemoteView(
                    activeDeviceName = selectedDevice?.friendlyName ?: "DLNA Smart TV",
                    targetIp = targetStbIp,
                    onTargetIpChange = { targetStbIp = it },
                    discoveredStbs = discoveredStbs,
                    isScanningStbs = isScanningStbs,
                    onScanStbs = viewModel::scanNetworkStbs,
                    onSendKey = { key -> viewModel.sendRemoteKey(key, targetStbIp) }
                )
            }
            4 -> StreamDoctorTab(
                url = quickCastUrl,
                report = streamHealthReport,
                isDiagnosing = isDiagnosingStream,
                onUrlChange = viewModel::setQuickCastUrl,
                onDiagnose = { u -> viewModel.runStreamDoctor(u) },
                onPlay = { u, t ->
                    viewModel.playInApp(u, t)
                    navigateToChannel()
                },
                onCast = { u, t ->
                    val dev = selectedDevice ?: devices.firstOrNull()
                    if (dev != null) {
                        viewModel.castToDevice(dev, u, t)
                    } else {
                        Toast.makeText(context, "Please select a DLNA TV on the Screens tab first", Toast.LENGTH_SHORT).show()
                    }
                }
            )
        }
    }
}

@Composable
private fun QuickCastTab(
    url: String,
    title: String,
    devices: List<Device>,
    selectedDevice: Device?,
    searching: Boolean,
    onUrlChange: (String) -> Unit,
    onTitleChange: (String) -> Unit,
    onSelectDevice: (Device?) -> Unit,
    onScanDevices: () -> Unit,
    onCastToDevice: (Device, String, String) -> Unit,
    onPlayLocally: (String, String) -> Unit
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp)
    ) {
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Cast Any Direct Video or Live Link",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
                Text(
                    text = "Supports HLS (.m3u8), DASH (.mpd), MP4, MKV, TS, FLV, and live web streams.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 4.dp, bottom = 12.dp)
                )

                // URL Input Field
                OutlinedTextField(
                    value = url,
                    onValueChange = onUrlChange,
                    label = { Text("Stream URL or Media Link") },
                    placeholder = { Text("https://example.com/stream.m3u8") },
                    trailingIcon = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            if (url.isNotBlank()) {
                                IconButton(onClick = { onUrlChange("") }) {
                                    Icon(Icons.Rounded.Clear, contentDescription = "Clear")
                                }
                            }
                            IconButton(onClick = {
                                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager
                                val item = clipboard?.primaryClip?.getItemAt(0)?.text?.toString()
                                if (!item.isNullOrBlank()) {
                                    onUrlChange(item)
                                }
                            }) {
                                Icon(Icons.Rounded.ContentPaste, contentDescription = "Paste")
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Title Input
                OutlinedTextField(
                    value = title,
                    onValueChange = onTitleChange,
                    label = { Text("Stream Title (Optional)") },
                    placeholder = { Text("My Live Channel") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true
                )

                // Format inspection badge
                if (url.isNotBlank()) {
                    val stream = StreamSnifferEngine.sniffUrl(url)
                    val formatLabel = stream?.streamType?.label ?: "Universal Stream"
                    Surface(
                        color = MaterialTheme.colorScheme.primaryContainer,
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.padding(top = 10.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.CheckCircle,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Detected format: $formatLabel",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Target Device Selection
        Text(
            text = "Select DLNA Target Screen",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            modifier = Modifier.padding(start = 4.dp, bottom = 8.dp)
        )

        if (devices.isEmpty()) {
            Surface(
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(20.dp)
                ) {
                    if (searching) {
                        CircularProgressIndicator(modifier = Modifier.size(28.dp))
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "Searching local network for Smart TVs & DLNA devices...",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    } else {
                        Text(
                            text = "No DLNA devices detected yet.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        OutlinedButton(onClick = onScanDevices) {
                            Icon(Icons.Rounded.Radar, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Scan Network")
                        }
                    }
                }
            }
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                devices.forEach { device ->
                    val isSelected = selectedDevice?.udn == device.udn
                    DlnaDeviceItem(
                        device = device,
                        isSelected = isSelected,
                        onClick = { onSelectDevice(device) }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Action Buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Button(
                onClick = {
                    val targetDev = selectedDevice ?: devices.firstOrNull()
                    if (targetDev != null && url.isNotBlank()) {
                        onCastToDevice(targetDev, url, title.ifBlank { "Direct Stream" })
                    } else if (targetDev == null) {
                        Toast.makeText(context, "Please select a DLNA TV first", Toast.LENGTH_SHORT).show()
                    }
                },
                enabled = url.isNotBlank() && devices.isNotEmpty(),
                modifier = Modifier
                    .weight(1f)
                    .height(50.dp),
                shape = RoundedCornerShape(14.dp)
            ) {
                Icon(Icons.Rounded.Cast, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Cast to DLNA TV", fontWeight = FontWeight.Bold)
            }

            FilledTonalButton(
                onClick = {
                    if (url.isNotBlank()) {
                        onPlayLocally(url, title.ifBlank { "Direct Stream" })
                    }
                },
                enabled = url.isNotBlank(),
                modifier = Modifier
                    .weight(1f)
                    .height(50.dp),
                shape = RoundedCornerShape(14.dp)
            ) {
                Icon(Icons.Rounded.PlayArrow, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Play in App", fontWeight = FontWeight.Bold)
            }
        }
    }
}

@SuppressLint("SetJavaScriptEnabled")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun WebSnifferTab(
    detectedStreams: List<DetectedStream>,
    devices: List<Device>,
    onStreamDetected: (DetectedStream) -> Unit,
    onClearStreams: () -> Unit,
    onCastStream: (DetectedStream, Device) -> Unit,
    onPlayStream: (DetectedStream) -> Unit
) {
    val context = LocalContext.current
    var inputUrl by remember { mutableStateOf("https://www.google.com") }
    var currentWebUrl by remember { mutableStateOf("https://www.google.com") }
    var webViewInstance by remember { mutableStateOf<WebView?>(null) }
    var pageTitle by remember { mutableStateOf("") }
    var pageLoadingProgress by remember { mutableIntStateOf(0) }
    var showStreamSheet by remember { mutableStateOf(false) }

    val presetSites = listOf(
        "Google" to "https://www.google.com",
        "Twitch" to "https://m.twitch.tv",
        "Dailymotion" to "https://www.dailymotion.com",
        "Vimeo" to "https://vimeo.com/watch"
    )

    Column(modifier = Modifier.fillMaxSize()) {
        // Browser URL & Control Bar
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 6.dp)
        ) {
            IconButton(
                onClick = { webViewInstance?.goBack() },
                enabled = webViewInstance?.canGoBack() == true,
                modifier = Modifier.size(36.dp)
            ) {
                Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = "Back")
            }
            IconButton(
                onClick = { webViewInstance?.goForward() },
                enabled = webViewInstance?.canGoForward() == true,
                modifier = Modifier.size(36.dp)
            ) {
                Icon(Icons.AutoMirrored.Rounded.ArrowForward, contentDescription = "Forward")
            }
            IconButton(
                onClick = { webViewInstance?.reload() },
                modifier = Modifier.size(36.dp)
            ) {
                Icon(Icons.Rounded.Refresh, contentDescription = "Reload")
            }

            OutlinedTextField(
                value = inputUrl,
                onValueChange = { inputUrl = it },
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp),
                shape = RoundedCornerShape(24.dp),
                singleLine = true,
                placeholder = { Text("Search or type URL...", fontSize = 12.sp) },
                trailingIcon = {
                    IconButton(onClick = {
                        val finalUrl = if (!inputUrl.startsWith("http://") && !inputUrl.startsWith("https://")) {
                            if (inputUrl.contains(".")) "https://$inputUrl"
                            else "https://www.google.com/search?q=${inputUrl.replace(" ", "+")}"
                        } else inputUrl
                        inputUrl = finalUrl
                        currentWebUrl = finalUrl
                        webViewInstance?.loadUrl(finalUrl)
                    }) {
                        Icon(Icons.Rounded.Search, contentDescription = "Go", tint = MaterialTheme.colorScheme.primary)
                    }
                }
            )
        }

        // Preset Chips
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 12.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            presetSites.forEach { (name, link) ->
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    modifier = Modifier.clickable {
                        inputUrl = link
                        currentWebUrl = link
                        webViewInstance?.loadUrl(link)
                    }
                ) {
                    Text(
                        text = name,
                        style = MaterialTheme.typography.labelSmall,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                    )
                }
            }
        }

        if (pageLoadingProgress in 1..99) {
            LinearProgressIndicator(
                modifier = Modifier.fillMaxWidth().height(2.dp)
            )
        }

        // Embedded Browser with Sniffer Interception
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            AndroidView(
                factory = { ctx ->
                    WebView(ctx).apply {
                        settings.javaScriptEnabled = true
                        settings.domStorageEnabled = true
                        settings.mediaPlaybackRequiresUserGesture = false
                        settings.cacheMode = WebSettings.LOAD_DEFAULT
                        settings.userAgentString = settings.userAgentString.replace("wv", "") // Better compatibility

                        webChromeClient = object : WebChromeClient() {
                            override fun onProgressChanged(view: WebView?, newProgress: Int) {
                                pageLoadingProgress = newProgress
                            }

                            override fun onReceivedTitle(view: WebView?, title: String?) {
                                pageTitle = title.orEmpty()
                            }
                        }

                        webViewClient = object : WebViewClient() {
                            override fun shouldInterceptRequest(
                                view: WebView?,
                                request: WebResourceRequest?
                            ): WebResourceResponse? {
                                StreamSnifferEngine.sniffRequest(request, pageTitle)?.let { stream ->
                                    onStreamDetected(stream)
                                }
                                return super.shouldInterceptRequest(view, request)
                            }

                            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                                url?.let { inputUrl = it }
                            }
                        }

                        loadUrl(currentWebUrl)
                        webViewInstance = this
                    }
                },
                update = { webViewInstance = it },
                modifier = Modifier.fillMaxSize()
            )
        }

        // V4 Floating Sniffer Dock (Matching Screenshot 1)
        com.m3u.smartphone.ui.business.cast.components.V4SnifferDock(
            detectedStreams = detectedStreams,
            onPlayStream = onPlayStream,
            onCastStream = { stream ->
                val target = devices.firstOrNull()
                if (target != null) {
                    onCastStream(stream, target)
                } else {
                    Toast.makeText(context, "No DLNA TV connected yet. Go to Screens tab to scan.", Toast.LENGTH_SHORT).show()
                }
            }
        )
    }

    // Sniffed Streams Bottom Sheet
    if (showStreamSheet) {
        val sheetState = rememberModalBottomSheetState()
        ModalBottomSheet(
            onDismissRequest = { showStreamSheet = false },
            sheetState = sheetState
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Captured Streams (${detectedStreams.size})",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                    OutlinedButton(onClick = onClearStreams) {
                        Text("Clear All")
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                LazyColumn(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(detectedStreams) { stream ->
                        Card(
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Surface(
                                        color = stream.streamType.badgeColor,
                                        shape = RoundedCornerShape(4.dp)
                                    ) {
                                        Text(
                                            text = stream.streamType.label,
                                            style = MaterialTheme.typography.labelSmall,
                                            color = Color.White,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = stream.title,
                                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }

                                Text(
                                    text = stream.url,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    maxLines = 2,
                                    overflow = TextOverflow.Ellipsis,
                                    modifier = Modifier.padding(vertical = 6.dp)
                                )

                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    FilledTonalButton(
                                        onClick = {
                                            val targetDev = devices.firstOrNull()
                                            if (targetDev != null) {
                                                onCastStream(stream, targetDev)
                                                showStreamSheet = false
                                            } else {
                                                Toast.makeText(context, "No DLNA devices found on network", Toast.LENGTH_SHORT).show()
                                            }
                                        },
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Icon(Icons.Rounded.Cast, contentDescription = null, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("Cast DLNA", fontSize = 12.sp)
                                    }

                                    Button(
                                        onClick = {
                                            onPlayStream(stream)
                                            showStreamSheet = false
                                        },
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Icon(Icons.Rounded.PlayArrow, contentDescription = null, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("Play in App", fontSize = 12.sp)
                                    }

                                    IconButton(onClick = {
                                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                        clipboard.setPrimaryClip(ClipData.newPlainText("Stream URL", stream.url))
                                        Toast.makeText(context, "Stream link copied!", Toast.LENGTH_SHORT).show()
                                    }) {
                                        Icon(Icons.Rounded.ContentCopy, contentDescription = "Copy")
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun DlnaDevicesTab(
    devices: List<Device>,
    searching: Boolean,
    castState: DlnaCastState,
    onScan: () -> Unit,
    onPause: (Device) -> Unit,
    onResume: (Device) -> Unit,
    onStop: (Device) -> Unit,
    onSeek: (Device, Long) -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "radar")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.9f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Radar Header Card
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(16.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(46.dp)
                        .scale(if (searching) pulseScale else 1f)
                        .background(MaterialTheme.colorScheme.primary, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Radar,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.size(24.dp)
                    )
                }
                Spacer(modifier = Modifier.width(14.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = if (searching) "Scanning Wi-Fi for DLNA TVs..." else "${devices.size} DLNA Screens Found",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                    Text(
                        text = "Samsung Tizen, LG webOS, Sony Bravia, Roku & Fire TV",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                OutlinedButton(onClick = onScan, enabled = !searching) {
                    Text(if (searching) "Scanning..." else "Scan")
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (devices.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Rounded.Tv,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                        modifier = Modifier.size(64.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "No DLNA TVs Found",
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = "Make sure your Smart TV is powered on\nand connected to the same Wi-Fi.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                }
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth().weight(1f)
            ) {
                items(devices) { device ->
                    DlnaDeviceItem(
                        device = device,
                        isSelected = when (castState) {
                            is DlnaCastState.Playing -> castState.device.udn == device.udn
                            is DlnaCastState.Paused -> castState.device.udn == device.udn
                            else -> false
                        },
                        onClick = {}
                    )
                }
            }
        }
    }
}

@Composable
private fun StreamDoctorTab(
    url: String,
    report: com.m3u.data.service.StreamHealthReport?,
    isDiagnosing: Boolean,
    onUrlChange: (String) -> Unit,
    onDiagnose: (String) -> Unit,
    onPlay: (String, String) -> Unit,
    onCast: (String, String) -> Unit
) {
    val scrollState = rememberScrollState()
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp)
    ) {
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Text(
                    text = "🩺 IPTV Stream Doctor & Health Analyzer",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
                Text(
                    text = "Pre-flight test for ping latency, real-world chunk throughput, anti-hotlink restrictions & HTTP codes.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 4.dp, bottom = 12.dp)
                )

                OutlinedTextField(
                    value = url,
                    onValueChange = onUrlChange,
                    label = { Text("Stream URL to Diagnose") },
                    placeholder = { Text("https://example.com/live.m3u8") },
                    trailingIcon = {
                        IconButton(onClick = {
                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager
                            val item = clipboard?.primaryClip?.getItemAt(0)?.text?.toString()
                            if (!item.isNullOrBlank()) {
                                onUrlChange(item)
                            }
                        }) {
                            Icon(Icons.Rounded.ContentPaste, contentDescription = "Paste")
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(14.dp))

                Button(
                    onClick = { onDiagnose(url) },
                    enabled = url.isNotBlank() && !isDiagnosing,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    if (isDiagnosing) {
                        CircularProgressIndicator(
                            color = MaterialTheme.colorScheme.onPrimary,
                            modifier = Modifier.size(20.dp),
                            strokeWidth = 2.dp
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text("Diagnosing Stream Health...")
                    } else {
                        Text("🔬 Run Stream Health Diagnostic", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        if (report != null) {
            Spacer(modifier = Modifier.height(16.dp))

            // Diagnostic Status Banner
            Surface(
                color = if (report.isOnline) Color(0xFF30D158).copy(alpha = 0.15f) else Color(0xFFFF453A).copy(alpha = 0.15f),
                shape = RoundedCornerShape(16.dp),
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    if (report.isOnline) Color(0xFF30D158).copy(alpha = 0.4f) else Color(0xFFFF453A).copy(alpha = 0.4f)
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = if (report.isOnline) "🟢 STREAM IS ONLINE & HEALTHY" else "🔴 STREAM ISSUES DETECTED",
                        fontWeight = FontWeight.ExtraBold,
                        style = MaterialTheme.typography.titleSmall,
                        color = if (report.isOnline) Color(0xFF30D158) else Color(0xFFFF453A)
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = report.diagnosticMessage,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Metrics Grid
            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                // Bandwidth Card
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)),
                    modifier = Modifier.weight(1f)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text("SPEED", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(
                            text = "${report.downloadSpeedMbps} Mbps",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = if (report.downloadSpeedMbps >= 5.0) Color(0xFF30D158) else Color(0xFFFF9F0A)
                        )
                    }
                }
                // Latency Card
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)),
                    modifier = Modifier.weight(1f)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text("LATENCY", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(
                            text = "${report.responseTimeMs} ms",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = if (report.responseTimeMs < 300) Color(0xFF30D158) else Color(0xFFFF9F0A)
                        )
                    }
                }
                // Status Code Card
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)),
                    modifier = Modifier.weight(1f)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text("HTTP CODE", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(
                            text = if (report.statusCode > 0) "${report.statusCode}" else "ERR",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = if (report.statusCode in 200..308) Color(0xFF30D158) else Color(0xFFFF453A)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Details Card
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Format:", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(report.streamFormat, style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold))
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Server IP:", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(report.serverIp, style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold))
                    }
                    if (report.contentType.isNotBlank()) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Content-Type:", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text(report.contentType, style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold))
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Quick Actions
            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Button(
                    onClick = { onPlay(report.url, "Diagnosed Stream") },
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Rounded.PlayArrow, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Play Stream")
                }
                FilledTonalButton(
                    onClick = { onCast(report.url, "Diagnosed Stream") },
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Rounded.Cast, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Cast to TV")
                }
            }
        }
    }
}


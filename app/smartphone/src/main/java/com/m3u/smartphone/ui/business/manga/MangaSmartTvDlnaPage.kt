package com.m3u.smartphone.ui.business.manga

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AspectRatio
import androidx.compose.material.icons.rounded.Audiotrack
import androidx.compose.material.icons.rounded.Cast
import androidx.compose.material.icons.rounded.CastConnected
import androidx.compose.material.icons.rounded.ClosedCaption
import androidx.compose.material.icons.rounded.ContentPaste
import androidx.compose.material.icons.rounded.FastForward
import androidx.compose.material.icons.rounded.FastRewind
import androidx.compose.material.icons.rounded.KeyboardArrowDown
import androidx.compose.material.icons.rounded.KeyboardArrowLeft
import androidx.compose.material.icons.rounded.KeyboardArrowRight
import androidx.compose.material.icons.rounded.KeyboardArrowUp
import androidx.compose.material.icons.rounded.Pause
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material.icons.rounded.Send
import androidx.compose.material.icons.rounded.Stop
import androidx.compose.material.icons.rounded.Tv
import androidx.compose.material.icons.rounded.VolumeDown
import androidx.compose.material.icons.rounded.VolumeMute
import androidx.compose.material.icons.rounded.VolumeUp
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.m3u.business.channel.DlnaCastState
import com.m3u.smartphone.ui.business.cast.SharedLinkManager
import net.mm2d.upnp.Device

/**
 * Page 4: DLNA Smart TV Overlord & Universal Link Sender
 * Turns any TV into a full Smart TV:
 * - Universal Link Sender (send ANY URL: YouTube, TikTok, MP4, HLS, cloud, etc.)
 * - DLNA Device Scanner & Auto-Connect
 * - Inked Manga D-Pad Smart TV Remote Controller
 */
@Composable
fun MangaSmartTvDlnaPage(
    devices: List<Device>,
    searching: Boolean,
    castState: DlnaCastState,
    onStartSearch: () -> Unit,
    onCastUrl: (url: String, title: String, device: Device) -> Unit,
    onPlayLocal: (url: String, title: String) -> Unit,
    onPlay: () -> Unit,
    onPause: () -> Unit,
    onStop: () -> Unit,
    onSeek: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var inputUrl by remember { mutableStateOf("") }
    var inputTitle by remember { mutableStateOf("") }
    var selectedDevice by remember { mutableStateOf<Device?>(devices.firstOrNull()) }
    var playbackPosition by remember { mutableFloatStateOf(0.25f) }
    var currentAspectRatio by remember { mutableStateOf("16:9") }

    // Auto-populate when external link is shared to the app
    LaunchedEffect(Unit) {
        val shared = SharedLinkManager.consumeSharedLink()
        if (!shared.isNullOrBlank()) {
            inputUrl = shared
            inputTitle = "Shared Video Stream"
            Toast.makeText(context, "Universal Link received! Ready to beam to TV.", Toast.LENGTH_SHORT).show()
        }
    }

    LaunchedEffect(devices) {
        if (selectedDevice == null && devices.isNotEmpty()) {
            selectedDevice = devices.first()
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MangaThemeTokens.InkBlack)
            .mangaHalftone()
            .verticalScroll(rememberScrollState())
            .padding(bottom = 80.dp)
    ) {
        // 1. Chapter Title Header
        MangaChapterHeader(
            chapterTitle = "Smart TV Overlord",
            actNumber = "Act 04",
            subtitle = "Universal Link Sender & DLNA Smart TV Remote Controller",
            badgeText = if (selectedDevice != null) "【TV CONNECTED! 📺】" else "【SCANNING RADAR】",
            badgeColor = if (selectedDevice != null) MangaThemeTokens.ToxicMangaGreen else MangaThemeTokens.ActionCrimson
        )

        // 2. Universal Link Sender Cannon (Send ANY link to TV!)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 4.dp)
                .mangaInkedPanel(
                    shape = RoundedCornerShape(12.dp),
                    borderWidth = 2.5.dp,
                    borderColor = MangaThemeTokens.ShonenGold,
                    shadowOffset = 4.dp
                )
                .background(MangaThemeTokens.InkPanel)
                .padding(14.dp)
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "⚡ UNIVERSAL LINK CANNON",
                        color = MangaThemeTokens.ShonenGold,
                        fontWeight = FontWeight.Black,
                        fontSize = 13.sp,
                        letterSpacing = 0.5.sp
                    )
                    MangaSoundEffectBadge(
                        text = "📋 PASTE",
                        color = MangaThemeTokens.PaperWhite,
                        textColor = Color.Black,
                        rotation = 0f,
                        onClick = {
                            val cm = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            val clip = cm.primaryClip?.getItemAt(0)?.text?.toString().orEmpty()
                            if (clip.isNotBlank()) {
                                inputUrl = clip
                                if (inputTitle.isBlank()) inputTitle = "Clipped Media Stream"
                                Toast.makeText(context, "Link pasted!", Toast.LENGTH_SHORT).show()
                            }
                        }
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = inputUrl,
                    onValueChange = { inputUrl = it },
                    placeholder = { Text("Paste any link: YouTube, TikTok, MP4, HLS (.m3u8), TS, WebM...", color = Color.Gray, fontSize = 11.sp) },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MangaThemeTokens.ShonenGold,
                        unfocusedBorderColor = Color.White.copy(alpha = 0.4f),
                        focusedContainerColor = MangaThemeTokens.InkSurface,
                        unfocusedContainerColor = MangaThemeTokens.InkSurface,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    ),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Beam to TV
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .mangaInkedPanel(
                                shape = RoundedCornerShape(8.dp),
                                borderWidth = 2.dp,
                                borderColor = Color.White,
                                shadowOffset = 3.dp
                            )
                            .background(MangaThemeTokens.ActionCrimson)
                            .clickable {
                                if (inputUrl.isBlank()) {
                                    Toast.makeText(context, "Please enter or paste a video link first!", Toast.LENGTH_SHORT).show()
                                    return@clickable
                                }
                                val target = selectedDevice ?: devices.firstOrNull()
                                if (target != null) {
                                    onCastUrl(inputUrl, inputTitle.ifBlank { "Omni-Stream" }, target)
                                    Toast.makeText(context, "Beaming stream to ${target.friendlyName}!", Toast.LENGTH_SHORT).show()
                                } else {
                                    Toast.makeText(context, "No DLNA TV selected. Scan devices below.", Toast.LENGTH_SHORT).show()
                                }
                            }
                            .padding(vertical = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Rounded.CastConnected, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(text = "📡 BEAM TO TV", color = Color.White, fontWeight = FontWeight.Black, fontSize = 11.sp)
                        }
                    }

                    // Play in App
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .mangaInkedPanel(
                                shape = RoundedCornerShape(8.dp),
                                borderWidth = 2.dp,
                                borderColor = Color.White,
                                shadowOffset = 3.dp
                            )
                            .background(MangaThemeTokens.NeoSpeedCyan)
                            .clickable {
                                if (inputUrl.isBlank()) {
                                    Toast.makeText(context, "Please enter or paste a video link first!", Toast.LENGTH_SHORT).show()
                                    return@clickable
                                }
                                onPlayLocal(inputUrl, inputTitle.ifBlank { "Omni-Stream" })
                            }
                            .padding(vertical = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Rounded.PlayArrow, contentDescription = null, tint = Color.Black, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(text = "▶️ PLAY INKED", color = Color.Black, fontWeight = FontWeight.Black, fontSize = 11.sp)
                        }
                    }
                }
            }
        }

        // 3. DLNA Device Scanner Radar
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 6.dp)
                .mangaInkedPanel(
                    shape = RoundedCornerShape(12.dp),
                    borderWidth = 2.dp,
                    borderColor = MangaThemeTokens.NeoSpeedCyan,
                    shadowOffset = 3.dp
                )
                .background(MangaThemeTokens.InkPanel)
                .padding(12.dp)
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Rounded.Tv, contentDescription = null, tint = MangaThemeTokens.NeoSpeedCyan, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "SMART TV DETECTOR RADAR (${devices.size} FOUND)",
                            color = Color.White,
                            fontWeight = FontWeight.Black,
                            fontSize = 12.sp
                        )
                    }

                    MangaSoundEffectBadge(
                        text = if (searching) "SCANNING..." else "🔄 RESCAN",
                        color = if (searching) MangaThemeTokens.ShonenGold else MangaThemeTokens.InkPanelElevated,
                        textColor = if (searching) Color.Black else Color.White,
                        rotation = 0f,
                        onClick = onStartSearch
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                if (devices.isEmpty()) {
                    Text(
                        text = "No DLNA Renderers detected yet. Make sure your Smart TV / Fire TV / Set-Top Box is on the same Wi-Fi.",
                        color = MangaThemeTokens.PaperMuted,
                        fontSize = 11.sp
                    )
                } else {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        devices.forEach { device ->
                            val isSelected = device.udn == selectedDevice?.udn
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isSelected) MangaThemeTokens.InkPanelElevated else MangaThemeTokens.InkSurface)
                                    .border(
                                        width = if (isSelected) 2.dp else 1.dp,
                                        color = if (isSelected) MangaThemeTokens.ToxicMangaGreen else Color.White.copy(alpha = 0.2f),
                                        shape = RoundedCornerShape(8.dp)
                                    )
                                    .clickable { selectedDevice = device }
                                    .padding(horizontal = 10.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column {
                                    Text(
                                        text = device.friendlyName,
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp
                                    )
                                    Text(
                                        text = "${device.modelName} • ${device.ipAddress}",
                                        color = MangaThemeTokens.PaperMuted,
                                        fontSize = 10.sp
                                    )
                                }

                                if (isSelected) {
                                    MangaSoundEffectBadge(
                                        text = "TARGET TV",
                                        color = MangaThemeTokens.ToxicMangaGreen,
                                        textColor = Color.Black
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // 4. Manga Inked Tactical Smart TV Remote Controller
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 6.dp)
                .mangaInkedPanel(
                    shape = RoundedCornerShape(14.dp),
                    borderWidth = 2.5.dp,
                    borderColor = Color.White,
                    shadowOffset = 4.dp
                )
                .background(MangaThemeTokens.InkPanel)
                .padding(14.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                MangaSoundEffectBadge(
                    text = "【UNIVERSAL SMART TV REMOTE】",
                    color = MangaThemeTokens.ShonenGold,
                    textColor = Color.Black
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Playback Control Buttons (Play, Pause, Stop)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RemoteTactileButton(icon = Icons.Rounded.FastRewind, label = "-10s") { onSeek(0) }
                    RemoteTactileButton(icon = Icons.Rounded.PlayArrow, label = "PLAY", color = MangaThemeTokens.ToxicMangaGreen, iconColor = Color.Black) { onPlay() }
                    RemoteTactileButton(icon = Icons.Rounded.Pause, label = "PAUSE", color = MangaThemeTokens.ShonenGold, iconColor = Color.Black) { onPause() }
                    RemoteTactileButton(icon = Icons.Rounded.Stop, label = "STOP", color = MangaThemeTokens.ActionCrimson) { onStop() }
                    RemoteTactileButton(icon = Icons.Rounded.FastForward, label = "+10s") { onSeek(1) }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Manga Inked D-Pad Controller
                Box(
                    modifier = Modifier
                        .size(170.dp)
                        .clip(CircleShape)
                        .background(MangaThemeTokens.InkSurface)
                        .border(3.dp, Color.White, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    // UP
                    IconButton(
                        onClick = { Toast.makeText(context, "D-Pad Up", Toast.LENGTH_SHORT).show() },
                        modifier = Modifier.align(Alignment.TopCenter).padding(top = 4.dp)
                    ) {
                        Icon(Icons.Rounded.KeyboardArrowUp, contentDescription = "Up", tint = Color.White, modifier = Modifier.size(32.dp))
                    }

                    // DOWN
                    IconButton(
                        onClick = { Toast.makeText(context, "D-Pad Down", Toast.LENGTH_SHORT).show() },
                        modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 4.dp)
                    ) {
                        Icon(Icons.Rounded.KeyboardArrowDown, contentDescription = "Down", tint = Color.White, modifier = Modifier.size(32.dp))
                    }

                    // LEFT
                    IconButton(
                        onClick = { Toast.makeText(context, "D-Pad Left", Toast.LENGTH_SHORT).show() },
                        modifier = Modifier.align(Alignment.CenterStart).padding(start = 4.dp)
                    ) {
                        Icon(Icons.Rounded.KeyboardArrowLeft, contentDescription = "Left", tint = Color.White, modifier = Modifier.size(32.dp))
                    }

                    // RIGHT
                    IconButton(
                        onClick = { Toast.makeText(context, "D-Pad Right", Toast.LENGTH_SHORT).show() },
                        modifier = Modifier.align(Alignment.CenterEnd).padding(end = 4.dp)
                    ) {
                        Icon(Icons.Rounded.KeyboardArrowRight, contentDescription = "Right", tint = Color.White, modifier = Modifier.size(32.dp))
                    }

                    // CENTER OK
                    Box(
                        modifier = Modifier
                            .size(54.dp)
                            .clip(CircleShape)
                            .background(MangaThemeTokens.ActionCrimson)
                            .border(2.dp, Color.White, CircleShape)
                            .clickable { Toast.makeText(context, "OK / ENTER Sent to TV", Toast.LENGTH_SHORT).show() },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "OK", color = Color.White, fontWeight = FontWeight.Black, fontSize = 14.sp)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Volume & Smart Tools Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Vol Controls
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        RemoteTactileButton(icon = Icons.Rounded.VolumeDown, label = "VOL -") {
                            Toast.makeText(context, "Volume -", Toast.LENGTH_SHORT).show()
                        }
                        RemoteTactileButton(icon = Icons.Rounded.VolumeUp, label = "VOL +") {
                            Toast.makeText(context, "Volume +", Toast.LENGTH_SHORT).show()
                        }
                        RemoteTactileButton(icon = Icons.Rounded.VolumeMute, label = "MUTE") {
                            Toast.makeText(context, "Mute toggled", Toast.LENGTH_SHORT).show()
                        }
                    }

                    // Smart Extras (Subtitles, Aspect Ratio, Audio Track)
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        RemoteTactileButton(icon = Icons.Rounded.ClosedCaption, label = "SUB") {
                            Toast.makeText(context, "Subtitles pushed to TV", Toast.LENGTH_SHORT).show()
                        }
                        RemoteTactileButton(icon = Icons.Rounded.AspectRatio, label = currentAspectRatio) {
                            currentAspectRatio = when (currentAspectRatio) {
                                "16:9" -> "21:9"
                                "21:9" -> "FILL"
                                else -> "16:9"
                            }
                            Toast.makeText(context, "Aspect Ratio: $currentAspectRatio", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }
    }
}

/**
 * Tactical Remote Tactile Button
 */
@Composable
private fun RemoteTactileButton(
    icon: ImageVector,
    label: String,
    color: Color = MangaThemeTokens.InkSurface,
    iconColor: Color = Color.White,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable { onClick() }
    ) {
        Box(
            modifier = Modifier
                .size(42.dp)
                .mangaInkedPanel(
                    shape = RoundedCornerShape(8.dp),
                    borderWidth = 1.5.dp,
                    borderColor = Color.White.copy(alpha = 0.5f),
                    shadowOffset = 2.dp
                )
                .background(color),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = label, tint = iconColor, modifier = Modifier.size(20.dp))
        }
        Spacer(modifier = Modifier.height(3.dp))
        Text(text = label, color = MangaThemeTokens.PaperMuted, fontSize = 9.sp, fontWeight = FontWeight.Bold)
    }
}

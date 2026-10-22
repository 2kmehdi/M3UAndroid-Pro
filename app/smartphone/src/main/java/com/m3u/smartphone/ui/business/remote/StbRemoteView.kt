package com.m3u.smartphone.ui.business.remote

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.FastForward
import androidx.compose.material.icons.rounded.FastRewind
import androidx.compose.material.icons.rounded.KeyboardArrowDown
import androidx.compose.material.icons.rounded.KeyboardArrowLeft
import androidx.compose.material.icons.rounded.KeyboardArrowRight
import androidx.compose.material.icons.rounded.KeyboardArrowUp
import androidx.compose.material.icons.rounded.Pause
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.PowerSettingsNew
import androidx.compose.material.icons.rounded.Remove
import androidx.compose.material.icons.rounded.Stop
import androidx.compose.material.icons.rounded.VolumeOff
import androidx.compose.material.icons.rounded.VolumeUp
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.TouchApp
import androidx.compose.material.icons.rounded.SportsEsports
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.ui.input.pointer.pointerInput
import com.m3u.data.service.DiscoveredStb
import com.m3u.data.service.StbKeyCode

/**
 * Zen Player & iOS-inspired STB / Smart TV Remote Control
 */
@Composable
fun StbRemoteView(
    onSendKey: (StbKeyCode) -> Unit,
    activeDeviceName: String = "DLNA Smart TV",
    targetIp: String = "",
    onTargetIpChange: (String) -> Unit = {},
    discoveredStbs: List<DiscoveredStb> = emptyList(),
    isScanningStbs: Boolean = false,
    onScanStbs: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    var showNumpad by remember { mutableStateOf(false) }
    var isTouchpadMode by remember { mutableStateOf(false) }
    var showStbScanner by remember { mutableStateOf(false) }

    val glassCardBg = Brush.verticalGradient(
        listOf(
            Color.White.copy(alpha = 0.08f),
            Color.White.copy(alpha = 0.03f)
        )
    )
    val glassCardBorder = Brush.verticalGradient(
        listOf(
            Color.White.copy(alpha = 0.18f),
            Color.White.copy(alpha = 0.04f)
        )
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(28.dp))
            .background(glassCardBg)
            .border(1.dp, glassCardBorder, RoundedCornerShape(28.dp))
            .padding(18.dp)
    ) {
        // --- 1. TOP STATUS & POWER ROW ---
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            // Power Button
            RemoteGlassButton(
                icon = Icons.Rounded.PowerSettingsNew,
                tint = Color(0xFFFF453A),
                size = 46.dp,
                onClick = { onSendKey(StbKeyCode.POWER) }
            )

            // Active Device Badge
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color.White.copy(alpha = 0.08f))
                    .border(1.dp, Color.White.copy(alpha = 0.12f), RoundedCornerShape(16.dp))
                    .clickable { showStbScanner = !showStbScanner }
                    .padding(horizontal = 14.dp, vertical = 6.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF30D158))
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (targetIp.isNotBlank()) "STB: $targetIp" else activeDeviceName,
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                        maxLines = 1
                    )
                }
            }

            // Mute Button
            RemoteGlassButton(
                icon = Icons.Rounded.VolumeOff,
                tint = Color(0xFFFFD60A),
                size = 46.dp,
                onClick = { onSendKey(StbKeyCode.MUTE) }
            )
        }

        // --- LAN STB SCANNER ACCORDION ---
        AnimatedVisibility(visible = showStbScanner) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color.White.copy(alpha = 0.05f))
                    .border(1.dp, Color.White.copy(alpha = 0.1f), RoundedCornerShape(16.dp))
                    .padding(12.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "📡 STB Network Receivers",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                        color = Color.White
                    )
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color(0xFF0A84FF).copy(alpha = 0.2f))
                            .border(1.dp, Color(0xFF0A84FF), RoundedCornerShape(10.dp))
                            .clickable { onScanStbs() }
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = if (isScanningStbs) "Scanning..." else "🔍 Scan LAN",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = Color(0xFF64D2FF)
                        )
                    }
                }
                if (isScanningStbs) {
                    Spacer(modifier = Modifier.height(8.dp))
                    LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                }
                if (discoveredStbs.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    discoveredStbs.forEach { stb ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color.White.copy(alpha = 0.06f))
                                .clickable { onTargetIpChange(stb.ip) }
                                .padding(8.dp)
                        ) {
                            Text(
                                text = "📺 ${stb.name} (${stb.protocol})",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.White,
                                modifier = Modifier.weight(1f)
                            )
                            if (targetIp == stb.ip) {
                                Text("CONNECTED", color = Color(0xFF30D158), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // --- MODE TOGGLE: D-PAD vs TOUCHPAD ---
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier
                .clip(RoundedCornerShape(20.dp))
                .background(Color.White.copy(alpha = 0.06f))
                .border(1.dp, Color.White.copy(alpha = 0.12f), RoundedCornerShape(20.dp))
                .padding(4.dp)
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .clip(RoundedCornerShape(16.dp))
                    .background(if (!isTouchpadMode) Color(0xFF0A84FF) else Color.Transparent)
                    .clickable { isTouchpadMode = false }
                    .padding(horizontal = 14.dp, vertical = 6.dp)
            ) {
                Text(
                    text = "🎮 Circular Wheel",
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                    color = if (!isTouchpadMode) Color.White else Color.White.copy(alpha = 0.6f)
                )
            }
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .clip(RoundedCornerShape(16.dp))
                    .background(if (isTouchpadMode) Color(0xFF0A84FF) else Color.Transparent)
                    .clickable { isTouchpadMode = true }
                    .padding(horizontal = 14.dp, vertical = 6.dp)
            ) {
                Text(
                    text = "👆 Swipe Trackpad",
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                    color = if (isTouchpadMode) Color.White else Color.White.copy(alpha = 0.6f)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // --- 2. CONTROLLER (D-PAD OR TRACKPAD) ---
        if (isTouchpadMode) {
            // Apple TV-style Glass Trackpad
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(210.dp)
                    .clip(RoundedCornerShape(32.dp))
                    .background(Color.White.copy(alpha = 0.05f))
                    .border(1.5.dp, Color.White.copy(alpha = 0.16f), RoundedCornerShape(32.dp))
                    .pointerInput(Unit) {
                        detectTapGestures(
                            onTap = { onSendKey(StbKeyCode.OK) }
                        )
                    }
                    .pointerInput(Unit) {
                        var totalDragX = 0f
                        var totalDragY = 0f
                        detectDragGestures(
                            onDrag = { change, dragAmount ->
                                change.consume()
                                totalDragX += dragAmount.x
                                totalDragY += dragAmount.y
                                if (totalDragX > 50f) {
                                    onSendKey(StbKeyCode.RIGHT)
                                    totalDragX = 0f
                                } else if (totalDragX < -50f) {
                                    onSendKey(StbKeyCode.LEFT)
                                    totalDragX = 0f
                                }
                                if (totalDragY > 50f) {
                                    onSendKey(StbKeyCode.DOWN)
                                    totalDragY = 0f
                                } else if (totalDragY < -50f) {
                                    onSendKey(StbKeyCode.UP)
                                    totalDragY = 0f
                                }
                            },
                            onDragEnd = {
                                totalDragX = 0f
                                totalDragY = 0f
                            }
                        )
                    }
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Rounded.TouchApp,
                        contentDescription = null,
                        tint = Color.White.copy(alpha = 0.4f),
                        modifier = Modifier.size(36.dp)
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Swipe to Navigate\nTap to Select (OK)",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.White.copy(alpha = 0.5f),
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                }
            }
        } else {
            // Circular D-Pad Controller
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(200.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.06f))
                    .border(1.5.dp, Color.White.copy(alpha = 0.15f), CircleShape)
            ) {
                // UP
                Icon(
                    imageVector = Icons.Rounded.KeyboardArrowUp,
                    contentDescription = "Up",
                    tint = Color.White.copy(alpha = 0.9f),
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .padding(top = 10.dp)
                        .size(40.dp)
                        .clip(CircleShape)
                        .clickable { onSendKey(StbKeyCode.UP) }
                )
                // DOWN
                Icon(
                    imageVector = Icons.Rounded.KeyboardArrowDown,
                    contentDescription = "Down",
                    tint = Color.White.copy(alpha = 0.9f),
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 10.dp)
                        .size(40.dp)
                        .clip(CircleShape)
                        .clickable { onSendKey(StbKeyCode.DOWN) }
                )
                // LEFT
                Icon(
                    imageVector = Icons.Rounded.KeyboardArrowLeft,
                    contentDescription = "Left",
                    tint = Color.White.copy(alpha = 0.9f),
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .padding(start = 10.dp)
                        .size(40.dp)
                        .clip(CircleShape)
                        .clickable { onSendKey(StbKeyCode.LEFT) }
                )
                // RIGHT
                Icon(
                    imageVector = Icons.Rounded.KeyboardArrowRight,
                    contentDescription = "Right",
                    tint = Color.White.copy(alpha = 0.9f),
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .padding(end = 10.dp)
                        .size(40.dp)
                        .clip(CircleShape)
                        .clickable { onSendKey(StbKeyCode.RIGHT) }
                )

                // CENTER OK BUTTON
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(76.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.linearGradient(
                                listOf(Color(0xFF0A84FF), Color(0xFF0051C6))
                            )
                        )
                        .clickable { onSendKey(StbKeyCode.OK) }
                ) {
                    Text(
                        text = "OK",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.ExtraBold,
                            letterSpacing = 1.sp
                        ),
                        color = Color.White
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // --- 3. VOLUME & CHANNEL ROCKERS ROW ---
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            // VOL COLUMN
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .width(62.dp)
                    .clip(RoundedCornerShape(22.dp))
                    .background(Color.White.copy(alpha = 0.07f))
                    .border(1.dp, Color.White.copy(alpha = 0.12f), RoundedCornerShape(22.dp))
                    .padding(vertical = 8.dp)
            ) {
                Icon(
                    imageVector = Icons.Rounded.Add,
                    contentDescription = "Vol +",
                    tint = Color.White,
                    modifier = Modifier
                        .size(32.dp)
                        .clickable { onSendKey(StbKeyCode.VOL_UP) }
                )
                Text(
                    text = "VOL",
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                    color = Color.White.copy(alpha = 0.6f),
                    modifier = Modifier.padding(vertical = 6.dp)
                )
                Icon(
                    imageVector = Icons.Rounded.Remove,
                    contentDescription = "Vol -",
                    tint = Color.White,
                    modifier = Modifier
                        .size(32.dp)
                        .clickable { onSendKey(StbKeyCode.VOL_DOWN) }
                )
            }

            // MIDDLE FUNCTIONS (MENU / BACK / EXIT / NUMPAD TOGGLE)
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    RemoteGlassButton(label = "MENU", onClick = { onSendKey(StbKeyCode.MENU) })
                    RemoteGlassButton(label = "EXIT", onClick = { onSendKey(StbKeyCode.EXIT) })
                }
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    RemoteGlassButton(label = "EPG", onClick = { onSendKey(StbKeyCode.EPG) })
                    RemoteGlassButton(label = if (showNumpad) "PAD ✕" else "123", onClick = { showNumpad = !showNumpad })
                }
            }

            // CH COLUMN
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .width(62.dp)
                    .clip(RoundedCornerShape(22.dp))
                    .background(Color.White.copy(alpha = 0.07f))
                    .border(1.dp, Color.White.copy(alpha = 0.12f), RoundedCornerShape(22.dp))
                    .padding(vertical = 8.dp)
            ) {
                Icon(
                    imageVector = Icons.Rounded.KeyboardArrowUp,
                    contentDescription = "CH +",
                    tint = Color.White,
                    modifier = Modifier
                        .size(32.dp)
                        .clickable { onSendKey(StbKeyCode.CH_UP) }
                )
                Text(
                    text = "CH",
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                    color = Color.White.copy(alpha = 0.6f),
                    modifier = Modifier.padding(vertical = 6.dp)
                )
                Icon(
                    imageVector = Icons.Rounded.KeyboardArrowDown,
                    contentDescription = "CH -",
                    tint = Color.White,
                    modifier = Modifier
                        .size(32.dp)
                        .clickable { onSendKey(StbKeyCode.CH_DOWN) }
                )
            }
        }

        // --- 4. OPTIONAL NUMPAD (0-9) ---
        AnimatedVisibility(
            visible = showNumpad,
            enter = expandVertically(),
            exit = shrinkVertically()
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(top = 16.dp)
            ) {
                val numRows = listOf(
                    listOf(StbKeyCode.NUM_1, StbKeyCode.NUM_2, StbKeyCode.NUM_3),
                    listOf(StbKeyCode.NUM_4, StbKeyCode.NUM_5, StbKeyCode.NUM_6),
                    listOf(StbKeyCode.NUM_7, StbKeyCode.NUM_8, StbKeyCode.NUM_9),
                    listOf(StbKeyCode.RECALL, StbKeyCode.NUM_0, StbKeyCode.INFO)
                )
                numRows.forEach { row ->
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.padding(vertical = 4.dp)
                    ) {
                        row.forEach { key ->
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier
                                    .size(48.dp)
                                    .clip(CircleShape)
                                    .background(Color.White.copy(alpha = 0.08f))
                                    .border(1.dp, Color.White.copy(alpha = 0.14f), CircleShape)
                                    .clickable { onSendKey(key) }
                            ) {
                                Text(
                                    text = key.commandName,
                                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                    color = Color.White
                                )
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // --- 5. MEDIA CONTROLS ROW ---
        Row(
            horizontalArrangement = Arrangement.SpaceEvenly,
            modifier = Modifier.fillMaxWidth()
        ) {
            RemoteGlassButton(icon = Icons.Rounded.FastRewind, onClick = { onSendKey(StbKeyCode.REWIND) })
            RemoteGlassButton(icon = Icons.Rounded.PlayArrow, tint = Color(0xFF30D158), onClick = { onSendKey(StbKeyCode.PLAY) })
            RemoteGlassButton(icon = Icons.Rounded.Pause, tint = Color(0xFFFF9F0A), onClick = { onSendKey(StbKeyCode.PAUSE) })
            RemoteGlassButton(icon = Icons.Rounded.Stop, tint = Color(0xFFFF453A), onClick = { onSendKey(StbKeyCode.STOP) })
            RemoteGlassButton(icon = Icons.Rounded.FastForward, onClick = { onSendKey(StbKeyCode.FAST_FORWARD) })
        }
    }
}

@Composable
private fun RemoteGlassButton(
    icon: ImageVector? = null,
    label: String? = null,
    tint: Color = Color.White,
    size: androidx.compose.ui.unit.Dp = 42.dp,
    onClick: () -> Unit
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .size(size)
            .clip(RoundedCornerShape(14.dp))
            .background(Color.White.copy(alpha = 0.08f))
            .border(1.dp, Color.White.copy(alpha = 0.12f), RoundedCornerShape(14.dp))
            .clickable { onClick() }
    ) {
        if (icon != null) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = tint,
                modifier = Modifier.size(22.dp)
            )
        } else if (label != null) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                color = tint,
                fontSize = 11.sp
            )
        }
    }
}

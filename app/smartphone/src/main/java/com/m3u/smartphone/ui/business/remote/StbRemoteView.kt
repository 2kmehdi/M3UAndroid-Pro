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
import com.m3u.data.service.StbKeyCode

/**
 * Zen Player & iOS-inspired STB / Smart TV Remote Control
 */
@Composable
fun StbRemoteView(
    onSendKey: (StbKeyCode) -> Unit,
    activeDeviceName: String = "DLNA Smart TV",
    modifier: Modifier = Modifier
) {
    var showNumpad by remember { mutableStateOf(false) }

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
                        text = activeDeviceName,
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

        Spacer(modifier = Modifier.height(20.dp))

        // --- 2. APPLE-STYLE CIRCULAR D-PAD CONTROLLER ---
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

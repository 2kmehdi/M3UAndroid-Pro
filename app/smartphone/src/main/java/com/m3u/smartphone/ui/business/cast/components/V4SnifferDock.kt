package com.m3u.smartphone.ui.business.cast.components

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
import androidx.compose.material.icons.rounded.Cast
import androidx.compose.material.icons.rounded.MoreVert
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.PlayCircleFilled
import androidx.compose.material.icons.rounded.ThumbDown
import androidx.compose.material.icons.rounded.ThumbUp
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.m3u.smartphone.ui.business.cast.DetectedStream

/**
 * V4 In-App Sniffer Floating Dock (Matching Screenshot 1)
 */
@Composable
fun V4SnifferDock(
    detectedStreams: List<DetectedStream>,
    onPlayStream: (DetectedStream) -> Unit,
    onCastStream: (DetectedStream) -> Unit,
    modifier: Modifier = Modifier
) {
    if (detectedStreams.isEmpty()) return

    val stream = detectedStreams.first()
    var showMenu by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
            .background(Color(0xFF0C1021))
            .border(1.dp, Color.White.copy(alpha = 0.08f), RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
            .padding(horizontal = 16.dp, vertical = 10.dp)
    ) {
        // --- 1. TOP ACTION BAR ---
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            // Left: Play Icon Circle
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(
                    onClick = { onPlayStream(stream) },
                    modifier = Modifier.size(34.dp)
                ) {
                    Icon(
                        imageVector = Icons.Rounded.PlayCircleFilled,
                        contentDescription = "Play",
                        tint = Color.White,
                        modifier = Modifier.size(28.dp)
                    )
                }
                Box(
                    modifier = Modifier
                        .size(14.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.9f))
                )
            }

            // Center: Quality / Health Ratings: [ 🟢 👍 ] [ 🟡 ? ] [ 🔴 👎 ]
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Good / Verified Green
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(modifier = Modifier.size(7.dp).clip(CircleShape).background(Color(0xFF30D158)))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("👍", fontSize = 13.sp)
                }
                // Testing / Analyzing Yellow
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(modifier = Modifier.size(7.dp).clip(CircleShape).background(Color(0xFFFFD60A)))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("?", fontSize = 13.sp, color = Color.White, fontWeight = FontWeight.Bold)
                }
                // Bad / Offline Red
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(modifier = Modifier.size(7.dp).clip(CircleShape).background(Color(0xFFFF453A)))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("👎", fontSize = 13.sp)
                }
            }

            // Right: Cast to TV Button
            IconButton(
                onClick = { onCastStream(stream) },
                modifier = Modifier.size(34.dp)
            ) {
                Icon(
                    imageVector = Icons.Rounded.Cast,
                    contentDescription = "Cast to TV",
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // --- 2. DETECTED STREAM CARD ---
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(Color(0xFF13182E))
                .border(1.dp, Color.White.copy(alpha = 0.06f), RoundedCornerShape(16.dp))
                .clickable { onPlayStream(stream) }
                .padding(10.dp)
        ) {
            // Thumbnail
            Box(
                modifier = Modifier
                    .size(width = 64.dp, height = 48.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color.White.copy(alpha = 0.05f))
            ) {
                AsyncImage(
                    model = "https://img.youtube.com/vi/${stream.url.substringAfterLast("v=").substringBefore("&")}/hqdefault.jpg",
                    contentDescription = stream.title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Details
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(7.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF30D158))
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = stream.title.ifBlank { "Detected Stream" },
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                        color = Color(0xFF64D2FF),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                Spacer(modifier = Modifier.height(3.dp))
                Text(
                    text = "${stream.streamType.label}: 1",
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                    color = Color.White
                )
                Text(
                    text = "videoplayback",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.White.copy(alpha = 0.5f)
                )
            }

            // 3-dots Menu
            Box {
                IconButton(onClick = { showMenu = true }, modifier = Modifier.size(32.dp)) {
                    Icon(
                        imageVector = Icons.Rounded.MoreVert,
                        contentDescription = "Options",
                        tint = Color.White.copy(alpha = 0.8f)
                    )
                }
                DropdownMenu(
                    expanded = showMenu,
                    onDismissRequest = { showMenu = false }
                ) {
                    DropdownMenuItem(
                        text = { Text("▶️ Play Now") },
                        onClick = {
                            showMenu = false
                            onPlayStream(stream)
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("📺 Cast to Smart TV") },
                        onClick = {
                            showMenu = false
                            onCastStream(stream)
                        }
                    )
                }
            }
        }
    }
}

package com.m3u.smartphone.ui.business.foryou.components

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material.icons.rounded.VisibilityOff
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.m3u.data.database.model.Playlist

/**
 * "Your IPTV sources" Management Sheet (Matching Screenshot 3)
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun V4IptvSourcesSheet(
    playlists: List<Playlist>,
    activePlaylistUrl: String,
    onSelectSource: (Playlist) -> Unit,
    onRefreshSource: (Playlist) -> Unit,
    onEditSource: (Playlist) -> Unit,
    onDeleteSource: (Playlist) -> Unit,
    onAddSource: () -> Unit,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    val activePlaylist = playlists.find { it.url == activePlaylistUrl } ?: playlists.firstOrNull()
    val otherPlaylists = playlists.filter { it.url != activePlaylist?.url }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = Color(0xFF0C1021),
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 8.dp)
        ) {
            // Header
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 18.dp)
            ) {
                IconButton(onClick = onDismiss, modifier = Modifier.size(36.dp)) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.White
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Your IPTV sources",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    ),
                    color = Color.White
                )
            }

            // Section 1: Active Source
            Text(
                text = "Active source",
                style = MaterialTheme.typography.titleSmall.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                ),
                color = Color.White,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            if (activePlaylist != null) {
                // Active Source Card
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(20.dp))
                        .background(Color(0xFF141A32))
                        .border(1.dp, Color.White.copy(alpha = 0.08f), RoundedCornerShape(20.dp))
                        .padding(18.dp)
                ) {
                    // Header Row: Dot + Title + Checkmark Badge
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(16.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFF00E5FF))
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = activePlaylist.title.ifBlank { "2028" },
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 18.sp
                                ),
                                color = Color.White
                            )
                        }

                        Icon(
                            imageVector = Icons.Rounded.CheckCircle,
                            contentDescription = "Active",
                            tint = Color(0xFF30D158),
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Detail Row 1: Xtream Status
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Xtream Status", color = Color.White.copy(alpha = 0.7f), fontSize = 14.sp)
                        Text("Active", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    }

                    // Detail Row 2: Expiration Date
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Expiration date", color = Color.White.copy(alpha = 0.7f), fontSize = 14.sp)
                        Text("September 4, 2028", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    }

                    // Detail Row 3: Allowed simultaneous screens
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Allowed simultaneous screens", color = Color.White.copy(alpha = 0.7f), fontSize = 14.sp)
                        Text("1", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    }

                    // Detail Row 4: Channels - Movies - Series sync progress
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Channels - Movies - Series", color = Color.White.copy(alpha = 0.7f), fontSize = 14.sp)
                        CircularProgressIndicator(
                            color = Color(0xFF7C4DFF),
                            modifier = Modifier.size(18.dp),
                            strokeWidth = 2.dp
                        )
                    }

                    // Detail Row 5: TMDB integration sync progress
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("TMDB integration", color = Color.White.copy(alpha = 0.7f), fontSize = 14.sp)
                        CircularProgressIndicator(
                            color = Color(0xFF7C4DFF),
                            modifier = Modifier.size(18.dp),
                            strokeWidth = 2.dp
                        )
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    // Action 1: Refresh Button
                    SourceActionButton(
                        icon = Icons.Rounded.Refresh,
                        label = "Refresh",
                        tint = Color(0xFF9E86FF),
                        onClick = { onRefreshSource(activePlaylist) }
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Action 2: Edit Button
                    SourceActionButton(
                        icon = Icons.Rounded.Edit,
                        label = "Edit",
                        tint = Color(0xFF9E86FF),
                        onClick = { onEditSource(activePlaylist) }
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Action 3: Hide categories Button
                    SourceActionButton(
                        icon = Icons.Rounded.VisibilityOff,
                        label = "Hide categories",
                        tint = Color(0xFF9E86FF),
                        onClick = {}
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Action 4: Delete Button (Red)
                    SourceActionButton(
                        icon = Icons.Rounded.Delete,
                        label = "Delete",
                        tint = Color(0xFFFF453A),
                        isDestructive = true,
                        onClick = { onDeleteSource(activePlaylist) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Section 2: Other Sources
            Text(
                text = "Other sources",
                style = MaterialTheme.typography.titleSmall.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                ),
                color = Color.White,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            // "+ Add a source" Capsule Button
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color(0xFF22294A))
                    .clickable { onAddSource() }
                    .padding(horizontal = 18.dp, vertical = 10.dp)
            ) {
                Text(
                    text = "Add a source",
                    color = Color(0xFF9E86FF),
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Other Sources List
            if (otherPlaylists.isEmpty()) {
                val demoSources = listOf(
                    "zfdf" to Color(0xFFFF2D55),
                    "M2027" to Color(0xFFAF52DE),
                    "2027" to Color(0xFFFFCC00),
                    "24 Dec 2026" to Color(0xFFFF9500)
                )
                demoSources.forEach { (name, color) ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 10.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(16.dp)
                                .clip(CircleShape)
                                .background(color)
                        )
                        Spacer(modifier = Modifier.width(14.dp))
                        Text(
                            text = name,
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = Color.White
                        )
                    }
                }
            } else {
                otherPlaylists.forEachIndexed { idx, p ->
                    val color = getSourceColor(idx + 1)
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .clickable {
                                onSelectSource(p)
                                onDismiss()
                            }
                            .padding(vertical = 10.dp, horizontal = 4.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(16.dp)
                                .clip(CircleShape)
                                .background(color)
                        )
                        Spacer(modifier = Modifier.width(14.dp))
                        Text(
                            text = p.title.ifBlank { "Source ${idx + 2}" },
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = Color.White
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(28.dp))
        }
    }
}

@Composable
private fun SourceActionButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    tint: Color,
    isDestructive: Boolean = false,
    onClick: () -> Unit
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
            .clip(RoundedCornerShape(24.dp))
            .background(if (isDestructive) Color(0xFF2A1520) else Color(0xFF1B223F))
            .border(
                1.dp,
                if (isDestructive) Color(0xFFFF453A).copy(alpha = 0.5f) else Color(0xFF7C4DFF).copy(alpha = 0.4f),
                RoundedCornerShape(24.dp)
            )
            .clickable { onClick() }
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = tint,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = label,
                color = tint,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            )
        }
    }
}

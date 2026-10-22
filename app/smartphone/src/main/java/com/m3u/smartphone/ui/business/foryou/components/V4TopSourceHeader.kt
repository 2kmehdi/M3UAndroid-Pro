package com.m3u.smartphone.ui.business.foryou.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material.icons.rounded.SwapHoriz
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
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

val V4_SOURCE_COLORS = listOf(
    Color(0xFF00E5FF), // Cyan
    Color(0xFFFF2D55), // Pink
    Color(0xFFAF52DE), // Purple
    Color(0xFFFFCC00), // Yellow
    Color(0xFFFF9500), // Orange
    Color(0xFF30D158), // Green
    Color(0xFF5856D6)  // Indigo
)

fun getSourceColor(index: Int): Color {
    return V4_SOURCE_COLORS[index % V4_SOURCE_COLORS.size]
}

/**
 * V4 Top Source App Bar (Matching Screenshots 2, 4, 5)
 * Displays colored active source dot, source name, switch icon, search, profile, settings.
 */
@Composable
fun V4TopSourceHeader(
    activeSourceName: String,
    activeSourceColor: Color,
    onOpenSourceSwitcher: () -> Unit,
    onOpenSearch: () -> Unit,
    onOpenSourcesManager: () -> Unit,
    onOpenSettings: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 18.dp, vertical = 8.dp)
    ) {
        // Left: Active Source Capsule with Switcher
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .clip(RoundedCornerShape(20.dp))
                .clickable { onOpenSourceSwitcher() }
                .padding(horizontal = 6.dp, vertical = 4.dp)
        ) {
            // Source Colored Dot
            Box(
                modifier = Modifier
                    .size(12.dp)
                    .clip(CircleShape)
                    .background(activeSourceColor)
            )

            Spacer(modifier = Modifier.width(10.dp))

            // Source Name
            Text(
                text = activeSourceName.ifBlank { "IPTV Source" },
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 17.sp
                ),
                color = Color.White
            )

            Spacer(modifier = Modifier.width(6.dp))

            // Switcher Arrows
            Icon(
                imageVector = Icons.Rounded.SwapHoriz,
                contentDescription = "Switch Source",
                tint = Color.White.copy(alpha = 0.7f),
                modifier = Modifier.size(20.dp)
            )
        }

        // Right Actions: Search, Account, Settings
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            IconButton(onClick = onOpenSearch, modifier = Modifier.size(38.dp)) {
                Icon(
                    imageVector = Icons.Rounded.Search,
                    contentDescription = "Search",
                    tint = Color.White.copy(alpha = 0.85f),
                    modifier = Modifier.size(22.dp)
                )
            }
            IconButton(onClick = onOpenSourcesManager, modifier = Modifier.size(38.dp)) {
                Icon(
                    imageVector = Icons.Rounded.Person,
                    contentDescription = "IPTV Sources",
                    tint = Color.White.copy(alpha = 0.85f),
                    modifier = Modifier.size(22.dp)
                )
            }
            IconButton(onClick = onOpenSettings, modifier = Modifier.size(38.dp)) {
                Icon(
                    imageVector = Icons.Rounded.Settings,
                    contentDescription = "Settings",
                    tint = Color.White.copy(alpha = 0.85f),
                    modifier = Modifier.size(22.dp)
                )
            }
        }
    }
}

/**
 * "Which IPTV source to use?" Bottom Sheet (Matching Screenshot 5)
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun V4SourceSwitcherBottomSheet(
    playlists: List<Playlist>,
    activePlaylistUrl: String,
    onSelectSource: (Playlist) -> Unit,
    onAddSource: () -> Unit,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = Color(0xFF101426),
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 8.dp)
        ) {
            // Header
            Text(
                text = "Which IPTV source to use?",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                ),
                color = Color.White,
                modifier = Modifier.padding(bottom = 20.dp, top = 4.dp)
            )

            // Sources List
            if (playlists.isEmpty()) {
                Text(
                    text = "No saved IPTV sources yet. Tap below to add one.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White.copy(alpha = 0.6f),
                    modifier = Modifier.padding(vertical = 16.dp)
                )
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(14.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(playlists.indices.toList()) { index ->
                        val playlist = playlists[index]
                        val isSelected = playlist.url == activePlaylistUrl || (activePlaylistUrl.isBlank() && index == 0)
                        val color = getSourceColor(index)

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(14.dp))
                                .clickable {
                                    onSelectSource(playlist)
                                    onDismiss()
                                }
                                .padding(vertical = 10.dp, horizontal = 4.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(16.dp)
                                        .clip(CircleShape)
                                        .background(color)
                                )
                                Spacer(modifier = Modifier.width(16.dp))
                                Text(
                                    text = playlist.title.ifBlank { "Source ${index + 1}" },
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 16.sp
                                    ),
                                    color = Color.White
                                )
                            }

                            if (isSelected) {
                                Icon(
                                    imageVector = Icons.Rounded.Check,
                                    contentDescription = "Selected",
                                    tint = Color.White,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Full-width Vibrant Purple "+ Add an IPTV source" Button
            Button(
                onClick = {
                    onDismiss()
                    onAddSource()
                },
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF7C4DFF)),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp)
            ) {
                Text(
                    text = "Add an IPTV source",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

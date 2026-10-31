package com.m3u.smartphone.ui.business.manga

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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.LiveTv
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.SwapHoriz
import androidx.compose.material.icons.rounded.Tv
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.m3u.data.database.model.Channel
import com.m3u.data.database.model.Playlist

/**
 * Page 1: Manga Inked IPTV Player & Xtream/M3U Hub
 */
@Composable
fun MangaIptvPlayerPage(
    playlists: List<Playlist>,
    activePlaylistUrl: String,
    channels: List<Channel>,
    onSelectPlaylist: (Playlist) -> Unit,
    onPlayChannel: (Channel) -> Unit,
    onAddPlaylist: () -> Unit,
    onRefreshPlaylist: (Playlist) -> Unit,
    modifier: Modifier = Modifier
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedDojo by remember { mutableStateOf("🥋 ALL DOJO") }
    var showSourceSheet by remember { mutableStateOf(false) }
    var isGridView by remember { mutableStateOf(true) }

    val activePlaylist = remember(playlists, activePlaylistUrl) {
        playlists.find { it.url == activePlaylistUrl } ?: playlists.firstOrNull()
    }

    val dojos = listOf("🥋 ALL DOJO", "⚡ LIVE ARENA", "🍿 CINEMA VAULT", "📜 ANIME SERIES", "⚽ SPORTS")

    val filteredChannels = remember(channels, searchQuery, selectedDojo) {
        channels.filter { channel ->
            val matchesSearch = searchQuery.isBlank() ||
                channel.title.contains(searchQuery, ignoreCase = true) ||
                channel.category.contains(searchQuery, ignoreCase = true)
            val matchesDojo = when (selectedDojo) {
                "⚡ LIVE ARENA" -> channel.category.contains("live", ignoreCase = true) || channel.category.contains("sport", ignoreCase = true)
                "🍿 CINEMA VAULT" -> channel.category.contains("movie", ignoreCase = true) || channel.category.contains("vod", ignoreCase = true)
                "📜 ANIME SERIES" -> channel.category.contains("anime", ignoreCase = true) || channel.category.contains("series", ignoreCase = true)
                "⚽ SPORTS" -> channel.category.contains("sport", ignoreCase = true) || channel.category.contains("bein", ignoreCase = true)
                else -> true
            }
            matchesSearch && matchesDojo
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MangaThemeTokens.InkBlack)
            .mangaHalftone()
    ) {
        // 1. Chapter Title Header
        MangaChapterHeader(
            chapterTitle = "IPTV Multiverse",
            actNumber = "Act 01",
            subtitle = "Direct Xtream Codes & M3U8 Matrix with Ki Buffering Engine",
            badgeText = "【DON! ⚡】",
            badgeColor = MangaThemeTokens.ShonenGold
        )

        // 2. Inked Source Bar & Profile Switcher Panel
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 4.dp)
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
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(12.dp)
                                .clip(CircleShape)
                                .background(MangaThemeTokens.ToxicMangaGreen)
                                .border(1.5.dp, Color.Black, CircleShape)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = (activePlaylist?.title ?: "XTREAM PRIME MATRIX").uppercase(),
                            color = Color.White,
                            fontWeight = FontWeight.Black,
                            fontSize = 14.sp
                        )
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        MangaSoundEffectBadge(
                            text = "【SWITCH ⇄】",
                            color = MangaThemeTokens.NeoSpeedCyan,
                            textColor = Color.Black,
                            rotation = 0f,
                            onClick = { showSourceSheet = true }
                        )
                        MangaSoundEffectBadge(
                            text = "【+ ADD】",
                            color = MangaThemeTokens.ActionCrimson,
                            textColor = Color.White,
                            rotation = 0f,
                            onClick = onAddPlaylist
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Ki Power Sync Meter
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "KI POWER SYNC: 100% (STABLE)",
                        color = MangaThemeTokens.ToxicMangaGreen,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp
                    )
                    Text(
                        text = "${filteredChannels.size} CHANNELS ONLINE",
                        color = MangaThemeTokens.PaperMuted,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(Color.Black)
                        .border(1.dp, Color.White, RoundedCornerShape(4.dp))
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(0.96f)
                            .height(8.dp)
                            .background(
                                Brush.horizontalGradient(
                                    listOf(MangaThemeTokens.ActionCrimson, MangaThemeTokens.ShonenGold, MangaThemeTokens.ToxicMangaGreen)
                                )
                            )
                    )
                }
            }
        }

        // 3. Search Bar with Inked Comic Border
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 6.dp)
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = {
                    Text(
                        "Search anime, live sports, movies...",
                        color = Color.White.copy(alpha = 0.5f),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                },
                leadingIcon = {
                    Icon(Icons.Rounded.Search, contentDescription = null, tint = MangaThemeTokens.ShonenGold)
                },
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color.White,
                    unfocusedBorderColor = Color.White.copy(alpha = 0.6f),
                    focusedContainerColor = MangaThemeTokens.InkSurface,
                    unfocusedContainerColor = MangaThemeTokens.InkSurface,
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White
                ),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(2.dp, Color.Black, RoundedCornerShape(10.dp))
            )
        }

        // 4. Dojos Filter Row (Manga Strip)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 14.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            dojos.forEach { dojo ->
                val isSelected = selectedDojo == dojo
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .border(
                            width = if (isSelected) 2.5.dp else 1.5.dp,
                            color = if (isSelected) MangaThemeTokens.ShonenGold else Color.White.copy(alpha = 0.4f),
                            shape = RoundedCornerShape(8.dp)
                        )
                        .background(if (isSelected) MangaThemeTokens.InkPanelElevated else MangaThemeTokens.InkSurface)
                        .clickable { selectedDojo = dojo }
                        .padding(horizontal = 12.dp, vertical = 7.dp)
                ) {
                    Text(
                        text = dojo,
                        color = if (isSelected) MangaThemeTokens.ShonenGold else Color.White,
                        fontWeight = if (isSelected) FontWeight.Black else FontWeight.Bold,
                        fontSize = 12.sp,
                        letterSpacing = 0.4.sp
                    )
                }
            }
        }

        // 5. Inked Comic Channel Grid / List
        if (filteredChannels.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    MangaSoundEffectBadge(
                        text = "【READY FOR BATTLE! ⚔️】",
                        color = MangaThemeTokens.ActionCrimson,
                        textColor = Color.White,
                        rotation = 0f
                    )
                    Text(
                        text = "No channels loaded in this Dojo yet.",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                    Text(
                        text = "Connect an Xtream Codes or M3U source via + ADD above.",
                        color = MangaThemeTokens.PaperMuted,
                        fontSize = 12.sp,
                        textAlign = TextAlign.Center
                    )
                }
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(horizontal = 14.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(filteredChannels) { ch ->
                    MangaChannelCard(
                        channel = ch,
                        onPlay = { onPlayChannel(ch) }
                    )
                }
            }
        }
    }

    // Source Switcher Modal Bottom Sheet
    if (showSourceSheet) {
        ModalBottomSheet(
            onDismissRequest = { showSourceSheet = false },
            sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
            containerColor = MangaThemeTokens.InkPanel
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp)
            ) {
                MangaSoundEffectBadge(
                    text = "【SELECT YOUR DOJO】",
                    color = MangaThemeTokens.ShonenGold,
                    textColor = Color.Black
                )
                Spacer(modifier = Modifier.height(14.dp))

                LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    items(playlists) { p ->
                        val isCurrent = p.url == activePlaylist?.url
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .mangaInkedPanel(
                                    shape = RoundedCornerShape(10.dp),
                                    borderWidth = if (isCurrent) 2.5.dp else 1.5.dp,
                                    borderColor = if (isCurrent) MangaThemeTokens.NeoSpeedCyan else Color.White.copy(alpha = 0.3f),
                                    shadowOffset = 3.dp
                                )
                                .background(if (isCurrent) MangaThemeTokens.InkPanelElevated else MangaThemeTokens.InkSurface)
                                .clickable {
                                    onSelectPlaylist(p)
                                    showSourceSheet = false
                                }
                                .padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text(
                                    text = p.title.ifBlank { "M3U Stream Pack" },
                                    color = Color.White,
                                    fontWeight = FontWeight.Black,
                                    fontSize = 14.sp
                                )
                                Text(
                                    text = p.url,
                                    color = MangaThemeTokens.PaperMuted,
                                    fontSize = 11.sp,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                            if (isCurrent) {
                                MangaSoundEffectBadge(text = "ACTIVE", color = MangaThemeTokens.ToxicMangaGreen, textColor = Color.Black)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .mangaInkedPanel(
                            shape = RoundedCornerShape(10.dp),
                            borderWidth = 2.dp,
                            borderColor = Color.White,
                            shadowOffset = 3.dp
                        )
                        .background(MangaThemeTokens.ActionCrimson)
                        .clickable {
                            showSourceSheet = false
                            onAddPlaylist()
                        }
                        .padding(vertical = 12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "➕ ADD NEW IPTV SOURCE",
                        color = Color.White,
                        fontWeight = FontWeight.Black,
                        fontSize = 13.sp
                    )
                }
            }
        }
    }
}

/**
 * Individual Channel Inked Comic Cell Card
 */
@Composable
private fun MangaChannelCard(
    channel: Channel,
    onPlay: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .mangaInkedPanel(
                shape = RoundedCornerShape(10.dp),
                borderWidth = 2.dp,
                borderColor = Color.White,
                shadowOffset = 3.dp
            )
            .background(MangaThemeTokens.InkPanel)
            .clickable { onPlay() }
            .padding(10.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // Live Badge & Cover
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(Color.Black)
                    .border(1.dp, Color.White.copy(alpha = 0.2f), RoundedCornerShape(6.dp)),
                contentAlignment = Alignment.Center
            ) {
                if (!channel.cover.isNullOrBlank()) {
                    AsyncImage(
                        model = channel.cover,
                        contentDescription = channel.title,
                        contentScale = ContentScale.Fit,
                        modifier = Modifier.fillMaxSize().padding(4.dp)
                    )
                } else {
                    Icon(
                        Icons.Rounded.LiveTv,
                        contentDescription = null,
                        tint = MangaThemeTokens.ShonenGold,
                        modifier = Modifier.size(28.dp)
                    )
                }

                // On Air Tag
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(4.dp)
                        .background(MangaThemeTokens.ActionCrimson, RoundedCornerShape(3.dp))
                        .padding(horizontal = 4.dp, vertical = 1.dp)
                ) {
                    Text(
                        text = "LIVE",
                        color = Color.White,
                        fontWeight = FontWeight.Black,
                        fontSize = 9.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = channel.title,
                color = Color.White,
                fontWeight = FontWeight.Black,
                fontSize = 12.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Text(
                text = channel.category.ifBlank { "General" },
                color = MangaThemeTokens.PaperMuted,
                fontSize = 10.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(6.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "HD 1080p",
                    color = MangaThemeTokens.NeoSpeedCyan,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold
                )
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .clip(CircleShape)
                        .background(MangaThemeTokens.ShonenGold)
                        .border(1.dp, Color.Black, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Rounded.PlayArrow, contentDescription = null, tint = Color.Black, modifier = Modifier.size(16.dp))
                }
            }
        }
    }
}

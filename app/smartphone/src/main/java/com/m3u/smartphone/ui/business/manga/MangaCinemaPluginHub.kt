package com.m3u.smartphone.ui.business.manga

import android.content.Context
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Extension
import androidx.compose.material.icons.rounded.Movie
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import net.mm2d.upnp.Device

/**
 * Data Model for Movie/TV Item from Plugins
 */
data class MangaMovieItem(
    val id: String,
    val title: String,
    val year: String,
    val rating: String,
    val genre: String,
    val posterUrl: String,
    val streamUrl: String,
    val pluginName: String
)

/**
 * Data Model for Extensible Movie/TV Plugins
 */
data class MangaMoviePlugin(
    val id: String,
    val name: String,
    val version: String,
    val author: String,
    val description: String,
    val isEnabled: Boolean = true,
    val movies: List<MangaMovieItem> = emptyList()
)

/**
 * Page 3: Manga Cinema & Dynamic Movies/TV Shows Plugin Hub
 * Users can browse VOD content and install, manage, and execute Movie/TV scraper plugins!
 */
@Composable
fun MangaCinemaPluginHub(
    devices: List<Device>,
    onPlayMovie: (MangaMovieItem) -> Unit,
    onCastMovie: (MangaMovieItem, Device) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var searchQuery by remember { mutableStateOf("") }
    var showPluginManagerSheet by remember { mutableStateOf(false) }
    var showAddPluginDialog by remember { mutableStateOf(false) }

    // Preloaded Extensible Plugins List
    val plugins = remember {
        mutableStateListOf(
            MangaMoviePlugin(
                id = "openflix_cinema",
                name = "🎬 OpenFlix Free Cinema Guild",
                version = "v2.4.0",
                author = "FlixCore Team",
                description = "Public domain cult classics, vintage noir, and open-source Hollywood cinema",
                isEnabled = true,
                movies = listOf(
                    MangaMovieItem(
                        id = "m1",
                        title = "Night of the Living Dead",
                        year = "1968",
                        rating = "★ 7.9",
                        genre = "Horror / Cult",
                        posterUrl = "https://images.unsplash.com/photo-1509281373149-e957c6296406?auto=format&fit=crop&w=400&q=80",
                        streamUrl = "https://archive.org/download/night_of_the_living_dead/night_of_the_living_dead_512kb.mp4",
                        pluginName = "OpenFlix Cinema"
                    ),
                    MangaMovieItem(
                        id = "m2",
                        title = "Metropolis: Cyber Inked",
                        year = "1927",
                        rating = "★ 8.3",
                        genre = "Sci-Fi Masterpiece",
                        posterUrl = "https://images.unsplash.com/photo-1534447677768-be436bb09401?auto=format&fit=crop&w=400&q=80",
                        streamUrl = "https://archive.org/download/Metropolis_1927/Metropolis_1927_512kb.mp4",
                        pluginName = "OpenFlix Cinema"
                    ),
                    MangaMovieItem(
                        id = "m3",
                        title = "Charade & Mystery",
                        year = "1963",
                        rating = "★ 7.9",
                        genre = "Thriller / Romance",
                        posterUrl = "https://images.unsplash.com/photo-1485846234645-a62644f84728?auto=format&fit=crop&w=400&q=80",
                        streamUrl = "https://archive.org/download/Charade_1963/Charade_1963_512kb.mp4",
                        pluginName = "OpenFlix Cinema"
                    )
                )
            ),
            MangaMoviePlugin(
                id = "animescrolls_vod",
                name = "🌸 AnimeScrolls OVA & Series Plugin",
                version = "v1.8.2",
                author = "NekoStream Guild",
                description = "Retro cyberpunk anime, high-octane mecha OVAs, and classic shonen movies",
                isEnabled = true,
                movies = listOf(
                    MangaMovieItem(
                        id = "a1",
                        title = "Cyber City Oedo: Shonen 2088",
                        year = "1991",
                        rating = "★ 8.4",
                        genre = "Cyberpunk / Action",
                        posterUrl = "https://images.unsplash.com/photo-1578632767115-351597cf2477?auto=format&fit=crop&w=400&q=80",
                        streamUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4",
                        pluginName = "AnimeScrolls"
                    ),
                    MangaMovieItem(
                        id = "a2",
                        title = "Mecha Warriors: Iron Steel",
                        year = "1994",
                        rating = "★ 8.1",
                        genre = "Mecha / Sci-Fi",
                        posterUrl = "https://images.unsplash.com/photo-1607604276583-eef5d076aa5f?auto=format&fit=crop&w=400&q=80",
                        streamUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ElephantsDream.mp4",
                        pluginName = "AnimeScrolls"
                    )
                )
            ),
            MangaMoviePlugin(
                id = "tmdb_meta_guild",
                name = "🌐 TMDB Cinematic Scraper Plugin",
                version = "v3.1.0",
                author = "Global Cine Collective",
                description = "Real-time movie metadata, trending worldwide box office, and official trailers",
                isEnabled = true,
                movies = listOf(
                    MangaMovieItem(
                        id = "t1",
                        title = "Dune: Inked Chronicle",
                        year = "2024",
                        rating = "★ 8.6",
                        genre = "Sci-Fi / Epic",
                        posterUrl = "https://images.unsplash.com/photo-1440404653325-ab127d49abc1?auto=format&fit=crop&w=400&q=80",
                        streamUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/TearsOfSteel.mp4",
                        pluginName = "TMDB Meta Guild"
                    ),
                    MangaMovieItem(
                        id = "t2",
                        title = "Oppenheimer: The Atomic Forge",
                        year = "2023",
                        rating = "★ 8.9",
                        genre = "Drama / History",
                        posterUrl = "https://images.unsplash.com/photo-1518709268805-4e9042af9f23?auto=format&fit=crop&w=400&q=80",
                        streamUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/Sintel.mp4",
                        pluginName = "TMDB Meta Guild"
                    )
                )
            )
        )
    }

    // Filtered Movies from all enabled plugins
    val allActiveMovies = remember(plugins, searchQuery) {
        plugins.filter { it.isEnabled }
            .flatMap { it.movies }
            .filter { movie ->
                searchQuery.isBlank() ||
                    movie.title.contains(searchQuery, ignoreCase = true) ||
                    movie.genre.contains(searchQuery, ignoreCase = true) ||
                    movie.pluginName.contains(searchQuery, ignoreCase = true)
            }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MangaThemeTokens.InkBlack)
            .mangaHalftone()
            .verticalScroll(rememberScrollState())
            .padding(bottom = 75.dp)
    ) {
        // 1. Chapter Title Header
        MangaChapterHeader(
            chapterTitle = "Manga Cinema & Plugins",
            actNumber = "Act 03",
            subtitle = "Extensible VOD Hub with Dynamic Community Movie & TV Show Addon Plugins",
            badgeText = "【${plugins.count { it.isEnabled }} PLUGINS ON】",
            badgeColor = MangaThemeTokens.MangaPurple
        )

        // 2. Plugin Manager Strip
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 4.dp)
                .mangaInkedPanel(
                    shape = RoundedCornerShape(12.dp),
                    borderWidth = 2.dp,
                    borderColor = MangaThemeTokens.MangaPurple,
                    shadowOffset = 3.dp
                )
                .background(MangaThemeTokens.InkPanel)
                .padding(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "PLUGIN REPOSITORY HUB",
                        color = Color.White,
                        fontWeight = FontWeight.Black,
                        fontSize = 13.sp
                    )
                    Text(
                        text = "Add custom movies, anime scrapers & VOD manifests",
                        color = MangaThemeTokens.PaperMuted,
                        fontSize = 11.sp
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    MangaSoundEffectBadge(
                        text = "⚙️ MANAGE",
                        color = MangaThemeTokens.MangaPurple,
                        textColor = Color.White,
                        rotation = 0f,
                        onClick = { showPluginManagerSheet = true }
                    )
                    MangaSoundEffectBadge(
                        text = "➕ ADD PLUGIN",
                        color = MangaThemeTokens.ShonenGold,
                        textColor = Color.Black,
                        rotation = 0f,
                        onClick = { showAddPluginDialog = true }
                    )
                }
            }
        }

        // 3. Search Bar
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 6.dp)
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = {
                    Text("Search across all installed movie plugins...", color = Color.Gray, fontSize = 12.sp)
                },
                leadingIcon = {
                    Icon(Icons.Rounded.Search, contentDescription = null, tint = MangaThemeTokens.NeoSpeedCyan)
                },
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color.White,
                    unfocusedBorderColor = Color.White.copy(alpha = 0.5f),
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

        // 4. Horizontal Inked Carousels per Active Plugin
        plugins.filter { it.isEnabled }.forEach { plugin ->
            val pluginMovies = plugin.movies.filter {
                searchQuery.isBlank() ||
                    it.title.contains(searchQuery, ignoreCase = true) ||
                    it.genre.contains(searchQuery, ignoreCase = true)
            }

            if (pluginMovies.isNotEmpty()) {
                Spacer(modifier = Modifier.height(10.dp))

                // Section Header
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = plugin.name.uppercase(),
                            color = Color.White,
                            fontWeight = FontWeight.Black,
                            fontSize = 13.sp,
                            letterSpacing = 0.5.sp
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Box(
                            modifier = Modifier
                                .background(MangaThemeTokens.InkPanelElevated, RoundedCornerShape(4.dp))
                                .border(1.dp, Color.White.copy(alpha = 0.2f), RoundedCornerShape(4.dp))
                                .padding(horizontal = 4.dp, vertical = 1.dp)
                        ) {
                            Text(text = plugin.version, color = MangaThemeTokens.NeoSpeedCyan, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    Text(
                        text = "${pluginMovies.size} TITLES",
                        color = MangaThemeTokens.PaperMuted,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Inked Horizontal Poster Cards
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState())
                        .padding(horizontal = 14.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    pluginMovies.forEach { movie ->
                        MangaMoviePosterCard(
                            movie = movie,
                            devices = devices,
                            onPlay = { onPlayMovie(movie) },
                            onCast = { dev -> onCastMovie(movie, dev) }
                        )
                    }
                }
            }
        }
    }

    // Modal Sheet: Plugin Manager
    if (showPluginManagerSheet) {
        ModalBottomSheet(
            onDismissRequest = { showPluginManagerSheet = false },
            sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
            containerColor = MangaThemeTokens.InkPanel
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    MangaSoundEffectBadge(
                        text = "【INSTALLED MOVIE & TV PLUGINS】",
                        color = MangaThemeTokens.MangaPurple,
                        textColor = Color.White
                    )
                    MangaSoundEffectBadge(
                        text = "➕ ADD",
                        color = MangaThemeTokens.ShonenGold,
                        textColor = Color.Black,
                        rotation = 0f,
                        onClick = {
                            showPluginManagerSheet = false
                            showAddPluginDialog = true
                        }
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    items(plugins) { plugin ->
                        val idx = plugins.indexOf(plugin)
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .mangaInkedPanel(
                                    shape = RoundedCornerShape(10.dp),
                                    borderWidth = 2.dp,
                                    borderColor = if (plugin.isEnabled) MangaThemeTokens.MangaPurple else Color.Gray,
                                    shadowOffset = 3.dp
                                )
                                .background(MangaThemeTokens.InkSurface)
                                .padding(12.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = plugin.name,
                                        color = Color.White,
                                        fontWeight = FontWeight.Black,
                                        fontSize = 13.sp
                                    )
                                    Text(
                                        text = plugin.description,
                                        color = MangaThemeTokens.PaperMuted,
                                        fontSize = 10.sp,
                                        maxLines = 2,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "By ${plugin.author} • ${plugin.version} • ${plugin.movies.size} streams",
                                        color = MangaThemeTokens.NeoSpeedCyan,
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }

                                Switch(
                                    checked = plugin.isEnabled,
                                    onCheckedChange = { checked ->
                                        plugins[idx] = plugin.copy(isEnabled = checked)
                                    },
                                    colors = SwitchDefaults.colors(
                                        checkedThumbColor = Color.White,
                                        checkedTrackColor = MangaThemeTokens.MangaPurple,
                                        uncheckedThumbColor = Color.Gray,
                                        uncheckedTrackColor = MangaThemeTokens.InkPanelElevated
                                    )
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    // Dialog: Add New Movie/TV Plugin
    if (showAddPluginDialog) {
        var pluginName by remember { mutableStateOf("") }
        var manifestUrl by remember { mutableStateOf("") }
        var sampleStreamUrl by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = { showAddPluginDialog = false },
            containerColor = MangaThemeTokens.InkPanel,
            title = {
                MangaSoundEffectBadge(
                    text = "【INSTALL MOVIE / TV PLUGIN】",
                    color = MangaThemeTokens.ShonenGold,
                    textColor = Color.Black
                )
            },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Paste a community VOD scraper manifest, Stremio addon, or custom movie repo URL:",
                        color = MangaThemeTokens.PaperMuted,
                        fontSize = 11.sp
                    )

                    OutlinedTextField(
                        value = pluginName,
                        onValueChange = { pluginName = it },
                        label = { Text("Plugin Name (e.g. CinemaDark VOD)", color = Color.Gray, fontSize = 11.sp) },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MangaThemeTokens.ShonenGold,
                            unfocusedBorderColor = Color.White.copy(alpha = 0.4f),
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        )
                    )

                    OutlinedTextField(
                        value = manifestUrl,
                        onValueChange = { manifestUrl = it },
                        label = { Text("Manifest / Repo URL", color = Color.Gray, fontSize = 11.sp) },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MangaThemeTokens.ShonenGold,
                            unfocusedBorderColor = Color.White.copy(alpha = 0.4f),
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        )
                    )

                    OutlinedTextField(
                        value = sampleStreamUrl,
                        onValueChange = { sampleStreamUrl = it },
                        label = { Text("Sample Playable Stream URL (Optional)", color = Color.Gray, fontSize = 11.sp) },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MangaThemeTokens.ShonenGold,
                            unfocusedBorderColor = Color.White.copy(alpha = 0.4f),
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        )
                    )
                }
            },
            confirmButton = {
                MangaSoundEffectBadge(
                    text = "【INSTALL PLUGIN】",
                    color = MangaThemeTokens.ToxicMangaGreen,
                    textColor = Color.Black,
                    rotation = 0f,
                    onClick = {
                        val name = pluginName.ifBlank { "Community Movie Addon" }
                        val stream = sampleStreamUrl.ifBlank { "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4" }
                        val newPlugin = MangaMoviePlugin(
                            id = "custom_${System.currentTimeMillis()}",
                            name = "⚡ $name",
                            version = "v1.0.0",
                            author = "Community Contributor",
                            description = "Custom installed movie addon via $manifestUrl",
                            isEnabled = true,
                            movies = listOf(
                                MangaMovieItem(
                                    id = "cust_m1",
                                    title = "$name Premiere",
                                    year = "2026",
                                    rating = "★ 9.0",
                                    genre = "Custom Plugin VOD",
                                    posterUrl = "https://images.unsplash.com/photo-1517604931442-7e0c8ed2963c?auto=format&fit=crop&w=400&q=80",
                                    streamUrl = stream,
                                    pluginName = name
                                )
                            )
                        )
                        plugins.add(newPlugin)
                        showAddPluginDialog = false
                        Toast.makeText(context, "Plugin '$name' installed successfully!", Toast.LENGTH_SHORT).show()
                    }
                )
            },
            dismissButton = {
                MangaSoundEffectBadge(
                    text = "CANCEL",
                    color = MangaThemeTokens.ActionCrimson,
                    textColor = Color.White,
                    rotation = 0f,
                    onClick = { showAddPluginDialog = false }
                )
            }
        )
    }
}

/**
 * Individual Movie Inked Poster Card
 */
@Composable
private fun MangaMoviePosterCard(
    movie: MangaMovieItem,
    devices: List<Device>,
    onPlay: () -> Unit,
    onCast: (Device) -> Unit
) {
    val context = LocalContext.current
    Box(
        modifier = Modifier
            .width(135.dp)
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
            // Poster Image
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color.Black)
                    .border(1.dp, Color.White.copy(alpha = 0.2f), RoundedCornerShape(8.dp))
                    .clickable { onPlay() }
            ) {
                AsyncImage(
                    model = movie.posterUrl,
                    contentDescription = movie.title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )

                // Rating Badge
                Box(
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(5.dp)
                        .background(MangaThemeTokens.InkBlack.copy(alpha = 0.85f), RoundedCornerShape(4.dp))
                        .border(1.dp, MangaThemeTokens.ShonenGold, RoundedCornerShape(4.dp))
                        .padding(horizontal = 4.dp, vertical = 2.dp)
                ) {
                    Text(text = movie.rating, color = MangaThemeTokens.ShonenGold, fontWeight = FontWeight.Black, fontSize = 9.sp)
                }

                // Play Icon overlay
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(5.dp)
                        .size(28.dp)
                        .clip(CircleShape)
                        .background(MangaThemeTokens.ActionCrimson)
                        .border(1.5.dp, Color.White, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Rounded.PlayArrow, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = movie.title,
                color = Color.White,
                fontWeight = FontWeight.Black,
                fontSize = 11.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Text(
                text = "${movie.year} • ${movie.genre}",
                color = MangaThemeTokens.PaperMuted,
                fontSize = 9.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(6.dp))

            // Direct Beam to TV Button
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(6.dp))
                    .background(MangaThemeTokens.InkPanelElevated)
                    .border(1.dp, MangaThemeTokens.NeoSpeedCyan, RoundedCornerShape(6.dp))
                    .clickable {
                        val target = devices.firstOrNull()
                        if (target != null) {
                            onCast(target)
                            Toast.makeText(context, "Beaming ${movie.title} to ${target.friendlyName}!", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(context, "No TV detected. Go to Chapter 4 (Smart TV).", Toast.LENGTH_SHORT).show()
                        }
                    }
                    .padding(vertical = 4.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "📡 BEAM TV",
                    color = MangaThemeTokens.NeoSpeedCyan,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Black
                )
            }
        }
    }
}

package com.m3u.smartphone.ui.business.manga

import android.widget.Toast
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Bolt
import androidx.compose.material.icons.rounded.Cast
import androidx.compose.material.icons.rounded.Movie
import androidx.compose.material.icons.rounded.Radar
import androidx.compose.material.icons.rounded.Tv
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.m3u.business.channel.DlnaCastState
import com.m3u.data.database.model.Channel
import com.m3u.data.database.model.Playlist
import com.m3u.data.service.StreamHealthReport
import com.m3u.smartphone.ui.business.cast.DetectedStream
import net.mm2d.upnp.Device

/**
 * Master Container for the Manga Inked Comic Panels Universe.
 * Seamlessly navigates across the 4 core pillars:
 * 1. IPTV Player (Xtream & M3U)
 * 2. Omni-Video Web Sniffer (All Media Interception)
 * 3. Manga Cinema & Dynamic Movies/TV Plugins
 * 4. Smart TV DLNA & Universal Link Cannon
 * Plus Ninja Power Tools!
 */
@Composable
fun MangaMasterContainer(
    playlists: List<Playlist>,
    activePlaylistUrl: String,
    channels: List<Channel>,
    detectedStreams: List<DetectedStream>,
    devices: List<Device>,
    searchingDlna: Boolean,
    castState: DlnaCastState,
    streamHealthReport: StreamHealthReport?,
    isDiagnosingStream: Boolean,
    onSelectPlaylist: (Playlist) -> Unit,
    onPlayChannel: (Channel) -> Unit,
    onAddPlaylist: () -> Unit,
    onRefreshPlaylist: (Playlist) -> Unit,
    onStreamDetected: (DetectedStream) -> Unit,
    onClearStreams: () -> Unit,
    onPlayStream: (DetectedStream) -> Unit,
    onCastStream: (DetectedStream, Device) -> Unit,
    onCastUrl: (String, String, Device) -> Unit,
    onPlayLocalUrl: (String, String) -> Unit,
    onStartDlnaSearch: () -> Unit,
    onRemotePlay: () -> Unit,
    onRemotePause: () -> Unit,
    onRemoteStop: () -> Unit,
    onRemoteSeek: (Long) -> Unit,
    onDiagnoseStream: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var selectedAct by remember { mutableIntStateOf(0) }
    var showNinjaToolsSheet by remember { mutableStateOf(false) }

    val acts = listOf(
        MangaAct(0, "IPTV DOJO", Icons.Rounded.Tv, MangaThemeTokens.ShonenGold),
        MangaAct(1, "OMNI-SNIFFER", Icons.Rounded.Radar, MangaThemeTokens.NeoSpeedCyan),
        MangaAct(2, "CINEMA & PLUGINS", Icons.Rounded.Movie, MangaThemeTokens.MangaPurple),
        MangaAct(3, "SMART TV BEAM", Icons.Rounded.Cast, MangaThemeTokens.ActionCrimson)
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MangaThemeTokens.InkBlack)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Top Manga Inked Chapter Tabs
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MangaThemeTokens.InkSurface)
                    .border(1.5.dp, Color.White, RoundedCornerShape(0.dp))
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 8.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                acts.forEach { act ->
                    val isSelected = selectedAct == act.id
                    Box(
                        modifier = Modifier
                            .mangaInkedPanel(
                                shape = RoundedCornerShape(8.dp),
                                borderWidth = if (isSelected) 2.5.dp else 1.5.dp,
                                borderColor = if (isSelected) act.color else Color.White.copy(alpha = 0.4f),
                                shadowOffset = if (isSelected) 3.dp else 1.dp
                            )
                            .background(if (isSelected) MangaThemeTokens.InkPanelElevated else MangaThemeTokens.InkPanel)
                            .clickable { selectedAct = act.id }
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                act.icon,
                                contentDescription = act.title,
                                tint = if (isSelected) act.color else Color.White.copy(alpha = 0.7f),
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = act.title,
                                color = if (isSelected) act.color else Color.White,
                                fontWeight = if (isSelected) FontWeight.Black else FontWeight.Bold,
                                fontStyle = FontStyle.Italic,
                                fontSize = 11.sp,
                                letterSpacing = 0.4.sp
                            )
                        }
                    }
                }

                // Ninja Tools Action Button
                Box(
                    modifier = Modifier
                        .mangaInkedPanel(
                            shape = RoundedCornerShape(8.dp),
                            borderWidth = 2.dp,
                            borderColor = MangaThemeTokens.ShonenGold,
                            shadowOffset = 2.dp
                        )
                        .background(MangaThemeTokens.InkPanel)
                        .clickable { showNinjaToolsSheet = true }
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Rounded.Bolt, contentDescription = null, tint = MangaThemeTokens.ShonenGold, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "TOOLS",
                            color = MangaThemeTokens.ShonenGold,
                            fontWeight = FontWeight.Black,
                            fontSize = 11.sp
                        )
                    }
                }
            }

            // Animated Screen Switching
            AnimatedContent(
                targetState = selectedAct,
                transitionSpec = { fadeIn() togetherWith fadeOut() },
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                label = "MangaActSwitcher"
            ) { actId ->
                when (actId) {
                    0 -> MangaIptvPlayerPage(
                        playlists = playlists,
                        activePlaylistUrl = activePlaylistUrl,
                        channels = channels,
                        onSelectPlaylist = onSelectPlaylist,
                        onPlayChannel = onPlayChannel,
                        onAddPlaylist = onAddPlaylist,
                        onRefreshPlaylist = onRefreshPlaylist,
                        modifier = Modifier.fillMaxSize()
                    )
                    1 -> MangaOmniSnifferPage(
                        detectedStreams = detectedStreams,
                        devices = devices,
                        onStreamDetected = onStreamDetected,
                        onClearStreams = onClearStreams,
                        onPlayStream = onPlayStream,
                        onCastStream = onCastStream,
                        modifier = Modifier.fillMaxSize()
                    )
                    2 -> MangaCinemaPluginHub(
                        devices = devices,
                        onPlayMovie = { movie ->
                            onPlayLocalUrl(movie.streamUrl, movie.title)
                        },
                        onCastMovie = { movie, dev ->
                            onCastUrl(movie.streamUrl, movie.title, dev)
                        },
                        modifier = Modifier.fillMaxSize()
                    )
                    3 -> MangaSmartTvDlnaPage(
                        devices = devices,
                        searching = searchingDlna,
                        castState = castState,
                        onStartSearch = onStartDlnaSearch,
                        onCastUrl = onCastUrl,
                        onPlayLocal = onPlayLocalUrl,
                        onPlay = onRemotePlay,
                        onPause = onRemotePause,
                        onStop = onRemoteStop,
                        onSeek = onRemoteSeek,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        }

        // Ninja Power Tools Sheet
        if (showNinjaToolsSheet) {
            MangaNinjaPowerToolsSheet(
                healthReport = streamHealthReport,
                isDiagnosing = isDiagnosingStream,
                onDiagnoseStream = onDiagnoseStream,
                onDismiss = { showNinjaToolsSheet = false }
            )
        }
    }
}

private data class MangaAct(
    val id: Int,
    val title: String,
    val icon: ImageVector,
    val color: Color
)

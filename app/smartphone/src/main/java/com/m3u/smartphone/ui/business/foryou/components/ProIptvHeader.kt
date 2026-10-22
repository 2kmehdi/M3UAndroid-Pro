package com.m3u.smartphone.ui.business.foryou.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Bolt
import androidx.compose.material.icons.rounded.Language
import androidx.compose.material.icons.rounded.LiveTv
import androidx.compose.material.icons.rounded.Movie
import androidx.compose.material.icons.rounded.Radar
import androidx.compose.material.icons.rounded.Tv
import androidx.compose.material.icons.rounded.VideoLibrary
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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

data class IptvProfile(
    val id: String,
    val name: String,
    val iconEmoji: String,
    val gradient: List<Color>
)

val DEFAULT_PROFILES = listOf(
    IptvProfile("all", "All Access", "👑", listOf(Color(0xFF673AB7), Color(0xFF512DA8))),
    IptvProfile("sports", "Live Sports", "⚽", listOf(Color(0xFF00C853), Color(0xFF1B5E20))),
    IptvProfile("cinema", "Cinema VOD", "🍿", listOf(Color(0xFFFF3D00), Color(0xFFDD2C00))),
    IptvProfile("series", "TV Series", "📺", listOf(Color(0xFF2979FF), Color(0xFF0D47A1))),
    IptvProfile("kids", "Family & Kids", "🧸", listOf(Color(0xFFFFD600), Color(0xFFFFAB00)))
)

@Composable
fun ProIptvHeader(
    totalPlaylists: Int,
    selectedProfileId: String,
    onSelectProfile: (String) -> Unit,
    selectedCategory: String,
    onSelectCategory: (String) -> Unit,
    onNavigateToCastHub: () -> Unit,
    modifier: Modifier = Modifier
) {
    val categories = listOf(
        "⚡ All" to Icons.Rounded.VideoLibrary,
        "📡 Live TV" to Icons.Rounded.LiveTv,
        "🍿 Movies" to Icons.Rounded.Movie,
        "🎞️ TV Shows" to Icons.Rounded.Tv
    )

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
    ) {
        // --- 1. PROFILES BAR (iOS Glass Avatar Capsules) ---
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "PROFILES",
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.4.sp
                ),
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.85f)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Box(
                modifier = Modifier
                    .height(1.dp)
                    .weight(1f)
                    .background(
                        Brush.horizontalGradient(
                            listOf(
                                MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f),
                                Color.Transparent
                            )
                        )
                    )
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            DEFAULT_PROFILES.forEach { profile ->
                val isSelected = selectedProfileId == profile.id
                val glassBg by animateColorAsState(
                    targetValue = if (isSelected) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.75f)
                    else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f),
                    animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
                    label = "glassProfileBg"
                )
                val glassBorder = if (isSelected) {
                    Brush.linearGradient(
                        listOf(
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.8f),
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
                        )
                    )
                } else {
                    Brush.linearGradient(
                        listOf(
                            Color.White.copy(alpha = 0.15f),
                            Color.White.copy(alpha = 0.04f)
                        )
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(22.dp))
                        .background(glassBg)
                        .border(1.dp, glassBorder, RoundedCornerShape(22.dp))
                        .clickable { onSelectProfile(profile.id) }
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(26.dp)
                                .clip(CircleShape)
                                .background(Brush.linearGradient(profile.gradient)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = profile.iconEmoji, fontSize = 13.sp)
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = profile.name,
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                                letterSpacing = 0.2.sp
                            ),
                            color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer
                            else MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // --- 2. APPLE IOS GLASS HERO CARD ---
        val heroGlassBg = Brush.verticalGradient(
            listOf(
                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f),
                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.25f)
            )
        )
        val heroGlassBorder = Brush.verticalGradient(
            listOf(
                Color.White.copy(alpha = 0.22f),
                Color.White.copy(alpha = 0.05f)
            )
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(elevation = 8.dp, shape = RoundedCornerShape(24.dp), ambientColor = Color.Black.copy(alpha = 0.2f))
                .clip(RoundedCornerShape(24.dp))
                .background(heroGlassBg)
                .border(1.dp, heroGlassBorder, RoundedCornerShape(24.dp))
                .padding(16.dp)
        ) {
            Column {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.linearGradient(
                                    listOf(Color(0xFF8E2DE2), Color(0xFF4A00E0))
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.LiveTv,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "M3UAndroid Pro Suite",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                letterSpacing = (-0.3).sp
                            )
                        )
                        Text(
                            text = "$totalPlaylists playlists • Universal Smart Cast & Streams",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Three iOS Control Center-style Frosted Tiles
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    IosGlassTile(
                        title = "Quick Cast",
                        subtitle = "Instant URL",
                        icon = Icons.Rounded.Bolt,
                        accentColor = Color(0xFFFF9500),
                        modifier = Modifier.weight(1f),
                        onClick = onNavigateToCastHub
                    )
                    IosGlassTile(
                        title = "Web Sniffer",
                        subtitle = "Auto Extract",
                        icon = Icons.Rounded.Language,
                        accentColor = Color(0xFF007AFF),
                        modifier = Modifier.weight(1f),
                        onClick = onNavigateToCastHub
                    )
                    IosGlassTile(
                        title = "DLNA Radar",
                        subtitle = "Smart TVs",
                        icon = Icons.Rounded.Radar,
                        accentColor = Color(0xFF34C759),
                        modifier = Modifier.weight(1f),
                        onClick = onNavigateToCastHub
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // --- 3. IOS SEGMENTED CAPSULE (All / Live TV / Movies / TV Shows) ---
        val segmentGlassBg = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)
        val segmentGlassBorder = Brush.verticalGradient(
            listOf(
                Color.White.copy(alpha = 0.18f),
                Color.White.copy(alpha = 0.04f)
            )
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(segmentGlassBg)
                .border(1.dp, segmentGlassBorder, RoundedCornerShape(16.dp))
                .padding(4.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                categories.forEach { (label, icon) ->
                    val isSelected = selectedCategory == label
                    val activeBg by animateColorAsState(
                        targetValue = if (isSelected) MaterialTheme.colorScheme.primaryContainer else Color.Transparent,
                        animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
                        label = "segmentActiveBg"
                    )

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(activeBg)
                            .clickable { onSelectCategory(label) }
                            .padding(horizontal = 14.dp, vertical = 7.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = icon,
                                contentDescription = null,
                                tint = if (isSelected) MaterialTheme.colorScheme.primary
                                else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                                modifier = Modifier.size(15.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = label,
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    letterSpacing = (-0.2).sp
                                ),
                                color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer
                                else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun IosGlassTile(
    title: String,
    subtitle: String,
    icon: ImageVector,
    accentColor: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val tileGlassBg = Brush.verticalGradient(
        listOf(
            Color.White.copy(alpha = 0.08f),
            Color.White.copy(alpha = 0.02f)
        )
    )
    val tileBorder = Brush.verticalGradient(
        listOf(
            Color.White.copy(alpha = 0.16f),
            Color.White.copy(alpha = 0.04f)
        )
    )

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(18.dp))
            .background(tileGlassBg)
            .border(1.dp, tileBorder, RoundedCornerShape(18.dp))
            .clickable { onClick() }
            .padding(vertical = 12.dp, horizontal = 6.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(34.dp)
                    .clip(CircleShape)
                    .background(accentColor.copy(alpha = 0.18f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = accentColor,
                    modifier = Modifier.size(18.dp)
                )
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.labelMedium.copy(
                    fontWeight = FontWeight.Bold,
                    letterSpacing = (-0.2).sp
                ),
                maxLines = 1
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                maxLines = 1
            )
        }
    }
}

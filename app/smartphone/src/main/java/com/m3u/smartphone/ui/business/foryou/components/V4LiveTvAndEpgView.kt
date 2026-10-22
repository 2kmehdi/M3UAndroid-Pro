package com.m3u.smartphone.ui.business.foryou.components

import androidx.compose.animation.animateColorAsState
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Tv
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.m3u.data.database.model.Channel

/**
 * V4 Live TV Screen & EPG Matrix (Matching Screenshot 4)
 */
@Composable
fun V4LiveTvAndEpgView(
    channels: List<Channel>,
    onChannelClick: (Channel) -> Unit,
    modifier: Modifier = Modifier
) {
    // Categories extracted from channels or defaults
    val categories = remember(channels) {
        val extracted = channels.mapNotNull { it.category.takeIf { c -> c.isNotBlank() } }.distinct()
        if (extracted.isNotEmpty()) extracted else listOf(
            "VIP UEFA EUROPA LEAGUE",
            "CHAMPIONS LEAGUE",
            "BEIN SPORTS",
            "FRANCE FHD",
            "ARABIC VIP",
            "UK ENTERTAINMENT",
            "SPAIN MOVIES"
        )
    }

    var selectedCategoryIndex by remember { mutableIntStateOf(0) }
    var selectedViewMode by remember { mutableIntStateOf(0) } // 0 = Channels, 1 = Columns EPG, 2 = Grid EPG

    val filteredChannels = remember(channels, selectedCategoryIndex, categories) {
        val currentCategory = categories.getOrNull(selectedCategoryIndex)
        if (currentCategory != null && channels.isNotEmpty()) {
            val list = channels.filter { it.category.equals(currentCategory, ignoreCase = true) }
            if (list.isNotEmpty()) list else channels.take(30)
        } else {
            channels.take(30)
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            // 1. Horizontally Scrollable Category Tabs with Animated Cyan Underline
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 14.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(18.dp)
            ) {
                categories.forEachIndexed { index, catName ->
                    val isSelected = index == selectedCategoryIndex
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .clickable { selectedCategoryIndex = index }
                            .padding(vertical = 4.dp)
                    ) {
                        Text(
                            text = catName.uppercase(),
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.Bold,
                                fontSize = 13.sp,
                                letterSpacing = 0.5.sp
                            ),
                            color = if (isSelected) Color.White else Color.White.copy(alpha = 0.55f)
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        // Active Underline Indicator
                        Box(
                            modifier = Modifier
                                .width(if (isSelected) 36.dp else 0.dp)
                                .height(2.5.dp)
                                .clip(RoundedCornerShape(2.dp))
                                .background(if (isSelected) Color(0xFF00E5FF) else Color.Transparent)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // 2. Channel List / EPG View
            if (filteredChannels.isEmpty()) {
                // Demo Channel items matching screenshot 4 if playlist is loading or empty
                val demoChannels = listOf(
                    "##### FRENCH #####" to "https://flagcdn.com/w160/fr.png",
                    "MATCH" to "https://upload.wikimedia.org/wikipedia/en/thumb/0/03/UEFA_Europa_League_logo_%282021%29.svg/1200px-UEFA_Europa_League_logo_%282021%29.svg.png",
                    "##### ARABIC #####" to "https://flagcdn.com/w160/sa.png",
                    "##### ENGLISH #####" to "https://flagcdn.com/w160/gb.png",
                    "##### SPAIN #####" to "https://flagcdn.com/w160/es.png"
                )
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    items(demoChannels) { (name, logo) ->
                        V4ChannelRowItem(
                            title = name,
                            logoUrl = logo,
                            onClick = {}
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    items(filteredChannels) { channel ->
                        V4ChannelRowItem(
                            title = channel.title,
                            logoUrl = channel.cover,
                            onClick = { onChannelClick(channel) }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(65.dp)) // Padding for bottom toggle & dock
        }

        // 3. Floating View Mode Segmented Capsule: [ Channels | Columns EPG | Grid EPG ]
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 12.dp)
                .clip(RoundedCornerShape(24.dp))
                .background(Color(0xFF10162A))
                .border(1.dp, Color.White.copy(alpha = 0.08f), RoundedCornerShape(24.dp))
                .padding(4.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                listOf("Channels", "Columns EPG", "Grid EPG").forEachIndexed { index, modeLabel ->
                    val isSelected = selectedViewMode == index
                    val activeBg by animateColorAsState(
                        targetValue = if (isSelected) Color(0xFF1B2848) else Color.Transparent,
                        label = "modeBg"
                    )
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(activeBg)
                            .clickable { selectedViewMode = index }
                            .padding(horizontal = 16.dp, vertical = 7.dp)
                    ) {
                        Text(
                            text = modeLabel,
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.Bold,
                                fontSize = 12.sp
                            ),
                            color = if (isSelected) Color(0xFF64D2FF) else Color.White.copy(alpha = 0.6f)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun V4ChannelRowItem(
    title: String,
    logoUrl: String,
    onClick: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .padding(vertical = 4.dp, horizontal = 4.dp)
    ) {
        // High-res Flag or Logo container
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(width = 66.dp, height = 44.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(Color.White.copy(alpha = 0.04f))
        ) {
            if (logoUrl.isNotBlank()) {
                AsyncImage(
                    model = logoUrl,
                    contentDescription = title,
                    contentScale = ContentScale.Fit,
                    modifier = Modifier.size(width = 62.dp, height = 40.dp)
                )
            } else {
                Icon(
                    imageVector = Icons.Rounded.Tv,
                    contentDescription = null,
                    tint = Color.White.copy(alpha = 0.3f),
                    modifier = Modifier.size(24.dp)
                )
            }
        }

        Spacer(modifier = Modifier.width(16.dp))

        // Bold Channel Title
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.ExtraBold,
                fontSize = 15.sp,
                letterSpacing = 0.5.sp
            ),
            color = Color.White,
            maxLines = 1
        )
    }
}

package com.m3u.smartphone.ui.business.foryou.components

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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.m3u.data.database.model.Channel

/**
 * V4 Series / VOD Screen (Matching Screenshot 2)
 */
@Composable
fun V4SeriesVodView(
    vodChannels: List<Channel>,
    onVodClick: (Channel) -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(bottom = 75.dp)
    ) {
        // Section 1: Recently added
        VodSectionRow(
            title = "Recently added",
            items = listOf(
                "Ice Wall" to "https://image.tmdb.org/t/p/w500/1E5baAaEse26fej7uHcjOgEE2t2.jpg",
                "JoJo Adventure" to "https://image.tmdb.org/t/p/w500/kZ1N8f9V86D4y6N0ZJ1Q6N9V86D.jpg",
                "Rondo" to "https://image.tmdb.org/t/p/w500/A088Z6F1wVw5m2n2J7Q6N9V86D.jpg"
            ),
            channels = vodChannels.take(8),
            onItemClick = onVodClick
        )

        Spacer(modifier = Modifier.height(18.dp))

        // Section 2: Srs | multi-lang - latest
        VodSectionRow(
            title = "Srs | multi-lang - latest",
            items = listOf(
                "A Knight of the Seven Kingdoms" to "https://image.tmdb.org/t/p/w500/7WsyChvgrmaXvH5pIeJ1b1062u2.jpg",
                "Crystal Lake" to "https://image.tmdb.org/t/p/w500/qJ2tW6WMUDux911r6m7haRef0WH.jpg",
                "The Pet Cemetery Murders" to "https://image.tmdb.org/t/p/w500/fiVW06jE7z9YnO4trhaMEdclSiC.jpg"
            ),
            channels = vodChannels.drop(8).take(8),
            onItemClick = onVodClick
        )

        Spacer(modifier = Modifier.height(18.dp))

        // Section 3: Srs | multi-lang - netflix (Shimmer Skeleton Cards)
        SkeletonSectionRow(title = "Srs | multi-lang - netflix")

        Spacer(modifier = Modifier.height(18.dp))

        // Section 4: Srs | multi-lang - series (Shimmer Skeleton Cards)
        SkeletonSectionRow(title = "Srs | multi-lang - series")
    }
}

@Composable
private fun VodSectionRow(
    title: String,
    items: List<Pair<String, String>>,
    channels: List<Channel>,
    onItemClick: (Channel) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.ExtraBold,
                fontSize = 16.sp
            ),
            color = Color.White,
            modifier = Modifier.padding(horizontal = 18.dp, vertical = 6.dp)
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            if (channels.isNotEmpty()) {
                channels.forEach { ch ->
                    VodPosterCard(
                        title = ch.title,
                        posterUrl = ch.cover ?: ch.logo,
                        onClick = { onItemClick(ch) }
                    )
                }
            } else {
                items.forEach { (name, url) ->
                    VodPosterCard(
                        title = name,
                        posterUrl = url,
                        onClick = {}
                    )
                }
            }
        }
    }
}

@Composable
private fun VodPosterCard(
    title: String,
    posterUrl: String?,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(width = 112.dp, height = 162.dp)
            .clip(RoundedCornerShape(18.dp))
            .background(Color(0xFF14192D))
            .border(1.dp, Color.White.copy(alpha = 0.08f), RoundedCornerShape(18.dp))
            .clickable { onClick() }
    ) {
        if (!posterUrl.isNullOrBlank()) {
            AsyncImage(
                model = posterUrl,
                contentDescription = title,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        } else {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(8.dp)
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                    color = Color.White.copy(alpha = 0.8f)
                )
            }
        }
    }
}

@Composable
private fun SkeletonSectionRow(title: String) {
    val transition = rememberInfiniteTransition(label = "shimmer")
    val translateAnim by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmerTranslate"
    )

    val shimmerBrush = Brush.linearGradient(
        colors = listOf(
            Color(0xFF151B32),
            Color(0xFF263056),
            Color(0xFF151B32)
        ),
        start = Offset(translateAnim - 500f, translateAnim - 500f),
        end = Offset(translateAnim, translateAnim)
    )

    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.ExtraBold,
                fontSize = 16.sp
            ),
            color = Color.White,
            modifier = Modifier.padding(horizontal = 18.dp, vertical = 6.dp)
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            repeat(4) {
                Box(
                    modifier = Modifier
                        .size(width = 112.dp, height = 162.dp)
                        .clip(RoundedCornerShape(18.dp))
                        .background(shimmerBrush)
                        .border(1.dp, Color.White.copy(alpha = 0.05f), RoundedCornerShape(18.dp))
                )
            }
        }
    }
}

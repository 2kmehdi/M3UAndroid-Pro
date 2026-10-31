package com.m3u.smartphone.ui.business.manga

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Manga Inked Comic Theme Tokens & Modifiers
 */
object MangaThemeTokens {
    val InkBlack = Color(0xFF08080C)
    val InkSurface = Color(0xFF12121A)
    val InkPanel = Color(0xFF191924)
    val InkPanelElevated = Color(0xFF222230)
    val InkBorder = Color(0xFFF9F7F1)
    val InkBorderDark = Color(0xFF000000)
    
    val PaperWhite = Color(0xFFFBF8F2)
    val PaperMuted = Color(0xFFCAC5B9)
    val Screentone = Color(0xFF2E2E3E)
    
    // Shonen Manga Action Accents
    val ActionCrimson = Color(0xFFFF2A2A)
    val ShonenGold = Color(0xFFFFD700)
    val NeoSpeedCyan = Color(0xFF00F0FF)
    val MangaPurple = Color(0xFFB026FF)
    val ToxicMangaGreen = Color(0xFF00FF66)
}

/**
 * Draws a genuine Manga Screentone Halftone pattern on the background.
 */
fun Modifier.mangaHalftone(
    dotColor: Color = MangaThemeTokens.Screentone.copy(alpha = 0.45f),
    dotRadius: Float = 1.8f,
    spacing: Float = 16f
): Modifier = this.drawBehind {
    val cols = (size.width / spacing).toInt() + 1
    val rows = (size.height / spacing).toInt() + 1
    for (i in 0..cols) {
        for (j in 0..rows) {
            val cx = i * spacing
            val cy = j * spacing
            drawCircle(
                color = dotColor,
                radius = dotRadius,
                center = Offset(cx, cy)
            )
        }
    }
}

/**
 * Inked Comic Panel modifier with thick ink border and a hard comic stamp drop shadow.
 */
fun Modifier.mangaInkedPanel(
    shape: Shape = RoundedCornerShape(12.dp),
    borderWidth: Dp = 2.5.dp,
    borderColor: Color = Color.White,
    shadowOffset: Dp = 4.dp,
    shadowColor: Color = MangaThemeTokens.InkBlack
): Modifier = this
    .drawBehind {
        // Draw hard offset comic stamp shadow
        drawRoundRect(
            color = shadowColor,
            topLeft = Offset(shadowOffset.toPx(), shadowOffset.toPx()),
            size = size
        )
    }
    .clip(shape)
    .border(borderWidth, borderColor, shape)

/**
 * Manga onomatopoeia sound-effect sticker/badge (e.g. 【DON! ⚡】, 【BAAAM! 💥】)
 */
@Composable
fun MangaSoundEffectBadge(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = MangaThemeTokens.ShonenGold,
    textColor: Color = Color.Black,
    rotation: Float = -4f,
    onClick: (() -> Unit)? = null
) {
    val clickableMod = if (onClick != null) Modifier.clickable { onClick() } else Modifier
    Box(
        modifier = modifier
            .rotate(rotation)
            .then(clickableMod)
            .mangaInkedPanel(
                shape = RoundedCornerShape(6.dp),
                borderWidth = 2.dp,
                borderColor = Color.Black,
                shadowOffset = 3.dp,
                shadowColor = Color.Black
            )
            .background(color)
            .padding(horizontal = 9.dp, vertical = 3.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = textColor,
            fontWeight = FontWeight.Black,
            fontStyle = FontStyle.Italic,
            fontSize = 11.sp,
            letterSpacing = 0.6.sp
        )
    }
}

/**
 * Dramatic Manga Chapter Banner Header
 */
@Composable
fun MangaChapterHeader(
    chapterTitle: String,
    actNumber: String,
    subtitle: String,
    badgeText: String? = null,
    badgeColor: Color = MangaThemeTokens.ActionCrimson,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 14.dp, vertical = 8.dp)
            .mangaInkedPanel(
                shape = RoundedCornerShape(14.dp),
                borderWidth = 3.dp,
                borderColor = Color.White,
                shadowOffset = 5.dp,
                shadowColor = Color.Black
            )
            .background(MangaThemeTokens.InkPanel)
            .mangaHalftone()
            .padding(14.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .background(MangaThemeTokens.ActionCrimson, RoundedCornerShape(4.dp))
                            .border(1.5.dp, Color.White, RoundedCornerShape(4.dp))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = actNumber.uppercase(),
                            color = Color.White,
                            fontWeight = FontWeight.Black,
                            fontSize = 10.sp,
                            letterSpacing = 1.sp
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = chapterTitle.uppercase(),
                        color = Color.White,
                        fontWeight = FontWeight.ExtraBold,
                        fontStyle = FontStyle.Italic,
                        fontSize = 16.sp,
                        letterSpacing = 0.8.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                if (badgeText != null) {
                    MangaSoundEffectBadge(
                        text = badgeText,
                        color = badgeColor,
                        textColor = Color.White,
                        rotation = 3f
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = subtitle,
                color = MangaThemeTokens.PaperMuted,
                fontWeight = FontWeight.Medium,
                fontSize = 12.sp,
                lineHeight = 16.sp,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

package com.m3u.smartphone.ui.business.manga

import android.widget.Toast
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
import androidx.compose.material.icons.rounded.Bolt
import androidx.compose.material.icons.rounded.Healing
import androidx.compose.material.icons.rounded.Security
import androidx.compose.material.icons.rounded.Speed
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.m3u.data.service.StreamHealthReport

/**
 * Manga Ninja Power Tools Modal Sheet:
 * - Stream Health Doctor (Ping, Bitrate, Anti-Hotlink verification)
 * - Turbo Pre-Buffer Engine (0-90s depth)
 * - Anti-Hotlink Header Spoofer
 */
@Composable
fun MangaNinjaPowerToolsSheet(
    healthReport: StreamHealthReport?,
    isDiagnosing: Boolean,
    onDiagnoseStream: (String) -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var testStreamUrl by remember { mutableStateOf("") }
    var bufferSeconds by remember { mutableFloatStateOf(45f) }
    var antiHotlinkEnabled by remember { mutableStateOf(true) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        containerColor = MangaThemeTokens.InkPanel
    ) {
        Column(
            modifier = modifier
                .fillMaxWidth()
                .padding(18.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                MangaSoundEffectBadge(
                    text = "【NINJA ARMORY & POWER TOOLS ⚔️】",
                    color = MangaThemeTokens.ShonenGold,
                    textColor = Color.Black
                )
                MangaSoundEffectBadge(
                    text = "CLOSE",
                    color = MangaThemeTokens.ActionCrimson,
                    textColor = Color.White,
                    rotation = 0f,
                    onClick = onDismiss
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // 1. Stream Doctor Tool
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .mangaInkedPanel(
                        shape = RoundedCornerShape(10.dp),
                        borderWidth = 2.dp,
                        borderColor = MangaThemeTokens.NeoSpeedCyan,
                        shadowOffset = 3.dp
                    )
                    .background(MangaThemeTokens.InkSurface)
                    .padding(12.dp)
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Rounded.Healing, contentDescription = null, tint = MangaThemeTokens.NeoSpeedCyan, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "IPTV STREAM DOCTOR (LATENCY & BITRATE)",
                            color = Color.White,
                            fontWeight = FontWeight.Black,
                            fontSize = 12.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = testStreamUrl,
                        onValueChange = { testStreamUrl = it },
                        placeholder = { Text("Stream URL to diagnose...", color = Color.Gray, fontSize = 11.sp) },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MangaThemeTokens.NeoSpeedCyan,
                            unfocusedBorderColor = Color.White.copy(alpha = 0.4f),
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        MangaSoundEffectBadge(
                            text = if (isDiagnosing) "ANALYZING KI..." else "⚡ RUN HEALTH CHECK",
                            color = MangaThemeTokens.ShonenGold,
                            textColor = Color.Black,
                            rotation = 0f,
                            onClick = {
                                if (testStreamUrl.isNotBlank()) {
                                    onDiagnoseStream(testStreamUrl)
                                } else {
                                    Toast.makeText(context, "Enter a stream URL to diagnose", Toast.LENGTH_SHORT).show()
                                }
                            }
                        )

                        if (isDiagnosing) {
                            CircularProgressIndicator(color = MangaThemeTokens.ShonenGold, modifier = Modifier.size(20.dp))
                        }
                    }

                    if (healthReport != null) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(MangaThemeTokens.InkPanel, RoundedCornerShape(6.dp))
                                .padding(8.dp)
                        ) {
                            Column {
                                Text(
                                    text = "Status: ${if (healthReport.isOnline) "🟢 ONLINE" else "🔴 ERROR ${healthReport.statusCode}"}",
                                    color = if (healthReport.isOnline) MangaThemeTokens.ToxicMangaGreen else MangaThemeTokens.ActionCrimson,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp
                                )
                                Text(
                                    text = "Latency: ${healthReport.responseTimeMs} ms • Bitrate: ${healthReport.downloadSpeedMbps} Mbps",
                                    color = MangaThemeTokens.PaperMuted,
                                    fontSize = 10.sp
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // 2. Zero-Stutter Turbo Buffer Slider
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .mangaInkedPanel(
                        shape = RoundedCornerShape(10.dp),
                        borderWidth = 2.dp,
                        borderColor = MangaThemeTokens.ShonenGold,
                        shadowOffset = 3.dp
                    )
                    .background(MangaThemeTokens.InkSurface)
                    .padding(12.dp)
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Rounded.Speed, contentDescription = null, tint = MangaThemeTokens.ShonenGold, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "TURBO PRE-LOAD BUFFER",
                                color = Color.White,
                                fontWeight = FontWeight.Black,
                                fontSize = 12.sp
                            )
                        }
                        Text(
                            text = "${bufferSeconds.toInt()}s DEPTH",
                            color = MangaThemeTokens.ShonenGold,
                            fontWeight = FontWeight.Black,
                            fontSize = 12.sp
                        )
                    }

                    Slider(
                        value = bufferSeconds,
                        onValueChange = { bufferSeconds = it },
                        valueRange = 10f..90f,
                        colors = SliderDefaults.colors(
                            thumbColor = MangaThemeTokens.ShonenGold,
                            activeTrackColor = MangaThemeTokens.ShonenGold,
                            inactiveTrackColor = Color.DarkGray
                        )
                    )

                    Text(
                        text = "Startup buffer: 1.5s • Max preload: ${bufferSeconds.toInt()}s. Eliminates sports buffering.",
                        color = MangaThemeTokens.PaperMuted,
                        fontSize = 10.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // 3. Anti-Hotlink Injection Switch
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .mangaInkedPanel(
                        shape = RoundedCornerShape(10.dp),
                        borderWidth = 2.dp,
                        borderColor = MangaThemeTokens.ToxicMangaGreen,
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
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Rounded.Security, contentDescription = null, tint = MangaThemeTokens.ToxicMangaGreen, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "ANTI-HOTLINK SPOOFING",
                                color = Color.White,
                                fontWeight = FontWeight.Black,
                                fontSize = 12.sp
                            )
                        }
                        Text(
                            text = "Injects Referer, Origin, & custom User-Agent to bypass 403 Forbidden on TV",
                            color = MangaThemeTokens.PaperMuted,
                            fontSize = 10.sp
                        )
                    }

                    Switch(
                        checked = antiHotlinkEnabled,
                        onCheckedChange = { antiHotlinkEnabled = it },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = MangaThemeTokens.ToxicMangaGreen
                        )
                    )
                }
            }
        }
    }
}

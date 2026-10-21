package com.m3u.smartphone.ui.business.channel.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.OpenInNew
import androidx.compose.material.icons.rounded.Cast
import androidx.compose.material.icons.rounded.CastConnected
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Pause
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.Stop
import androidx.compose.material.icons.rounded.VolumeUp
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedIconButton
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.m3u.business.channel.DlnaCastState
import com.m3u.business.channel.brandCategory
import com.m3u.core.foundation.util.basic.title
import com.m3u.i18n.R.string
import com.m3u.smartphone.ui.material.components.mask.MaskState
import com.m3u.smartphone.ui.material.model.LocalSpacing
import net.mm2d.upnp.Device

@Composable
internal fun DlnaDevicesBottomSheet(
    isDevicesVisible: Boolean,
    devices: List<Device>,
    searching: Boolean,
    maskState: MaskState,
    onDismiss: () -> Unit,
    connectDlnaDevice: (device: Device) -> Unit,
    openInExternalPlayer: () -> Unit,
    modifier: Modifier = Modifier,
    castState: DlnaCastState = DlnaCastState.Idle,
    onDisconnect: (Device) -> Unit = {},
    onPause: (Device) -> Unit = {},
    onResume: (Device) -> Unit = {},
    onSeek: (Device, Long) -> Unit = { _, _ -> },
    onSetVolume: (Device, Int) -> Unit = { _, _ -> }
) {
    val spacing = LocalSpacing.current
    val state = rememberModalBottomSheetState()
    val openInExternalPlayerString = stringResource(string.feat_channel_open_in_external_app)

    LaunchedEffect(isDevicesVisible) {
        if (isDevicesVisible) state.show()
        else state.hide()
    }

    if (isDevicesVisible) {
        ModalBottomSheet(
            sheetState = state,
            onDismissRequest = onDismiss,
            modifier = modifier
        ) {
            LaunchedEffect(devices, state.isVisible) {
                if (state.isVisible) state.expand()
            }
            LaunchedEffect(Unit) {
                maskState.sleep()
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp)
            ) {
                // Header Bar
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(spacing.small),
                    modifier = Modifier.padding(
                        horizontal = spacing.medium,
                        vertical = spacing.small
                    )
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Cast,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(24.dp)
                    )
                    Text(
                        text = stringResource(string.feat_channel_dialog_dlna_devices),
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleMedium,
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    AnimatedVisibility(visible = searching) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            strokeWidth = 2.dp
                        )
                    }
                }

                // Active Casting Remote Control Card (When actively casting to TV)
                when (castState) {
                    is DlnaCastState.Playing -> {
                        DlnaActiveCastingCard(
                            device = castState.device,
                            title = castState.title,
                            isPlaying = true,
                            positionMs = castState.positionMs,
                            durationMs = castState.durationMs,
                            onPause = { onPause(castState.device) },
                            onResume = { onResume(castState.device) },
                            onDisconnect = { onDisconnect(castState.device) },
                            onSeek = { pos -> onSeek(castState.device, pos) }
                        )
                    }
                    is DlnaCastState.Paused -> {
                        DlnaActiveCastingCard(
                            device = castState.device,
                            title = castState.title,
                            isPlaying = false,
                            positionMs = 0L,
                            durationMs = 0L,
                            onPause = { onPause(castState.device) },
                            onResume = { onResume(castState.device) },
                            onDisconnect = { onDisconnect(castState.device) },
                            onSeek = { pos -> onSeek(castState.device, pos) }
                        )
                    }
                    is DlnaCastState.Connecting -> {
                        Surface(
                            color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f),
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = spacing.medium, vertical = 6.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(14.dp)
                            ) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(22.dp),
                                    strokeWidth = 2.5.dp
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(
                                        text = "Connecting to ${castState.device.friendlyName.ifBlank { "Smart TV" }}...",
                                        fontWeight = FontWeight.SemiBold,
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                    Text(
                                        text = "Negotiating stream format with TV...",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                    else -> {}
                }

                LazyColumn(
                    modifier = Modifier.sizeIn(maxHeight = 360.dp)
                ) {
                    item {
                        ListItem(
                            headlineContent = {
                                Text(
                                    text = openInExternalPlayerString.title(),
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            },
                            trailingContent = {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Rounded.OpenInNew,
                                    contentDescription = openInExternalPlayerString
                                )
                            },
                            modifier = Modifier.clickable { openInExternalPlayer() }
                        )
                    }

                    if (devices.isEmpty() && !searching) {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 32.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "No DLNA TVs found on this Wi-Fi network.\nMake sure your TV and phone are on the same Wi-Fi.",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                )
                            }
                        }
                    }

                    items(devices) { device ->
                        val isCurrentTarget = when (castState) {
                            is DlnaCastState.Playing -> castState.device.udn == device.udn
                            is DlnaCastState.Paused -> castState.device.udn == device.udn
                            is DlnaCastState.Connecting -> castState.device.udn == device.udn
                            else -> false
                        }
                        DlnaDeviceItem(
                            device = device,
                            isSelected = isCurrentTarget,
                            onClick = { connectDlnaDevice(device) }
                        )
                    }

                    item {
                        Spacer(modifier = Modifier.navigationBarsPadding())
                    }
                }
            }
        }
    }
}

@Composable
private fun DlnaActiveCastingCard(
    device: Device,
    title: String,
    isPlaying: Boolean,
    positionMs: Long,
    durationMs: Long,
    onPause: () -> Unit,
    onResume: () -> Unit,
    onDisconnect: () -> Unit,
    onSeek: (Long) -> Unit
) {
    val brand = device.brandCategory
    val friendlyName = device.friendlyName.ifBlank { brand }

    Surface(
        color = MaterialTheme.colorScheme.primaryContainer,
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
    ) {
        Column(
            modifier = Modifier.padding(14.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .background(MaterialTheme.colorScheme.primary, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Rounded.CastConnected,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Now Casting to $friendlyName",
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Text(
                        text = title,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f),
                        maxLines = 1
                    )
                }
                OutlinedIconButton(
                    onClick = onDisconnect,
                    colors = IconButtonDefaults.outlinedIconButtonColors(
                        contentColor = MaterialTheme.colorScheme.error
                    ),
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Close,
                        contentDescription = "Disconnect Cast",
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            if (durationMs > 0L) {
                var sliderValue by remember(positionMs) { mutableFloatStateOf(positionMs.toFloat()) }
                Slider(
                    value = sliderValue,
                    onValueChange = { sliderValue = it },
                    onValueChangeFinished = { onSeek(sliderValue.toLong()) },
                    valueRange = 0f..durationMs.toFloat(),
                    modifier = Modifier.padding(top = 4.dp)
                )
            }

            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp)
            ) {
                FilledIconButton(
                    onClick = if (isPlaying) onPause else onResume,
                    modifier = Modifier.size(46.dp)
                ) {
                    Icon(
                        imageVector = if (isPlaying) Icons.Rounded.Pause else Icons.Rounded.PlayArrow,
                        contentDescription = if (isPlaying) "Pause" else "Play",
                        modifier = Modifier.size(24.dp)
                    )
                }
                Spacer(modifier = Modifier.width(16.dp))
                FilledIconButton(
                    onClick = onDisconnect,
                    colors = IconButtonDefaults.filledIconButtonColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer,
                        contentColor = MaterialTheme.colorScheme.onErrorContainer
                    ),
                    modifier = Modifier.size(46.dp)
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Stop,
                        contentDescription = "Stop Casting",
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }
    }
}
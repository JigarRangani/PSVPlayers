package com.jigar.psvplayer.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.media3.exoplayer.ExoPlayer
import kotlinx.coroutines.delay
import java.util.concurrent.TimeUnit

@Composable
fun PlayerControls(player: ExoPlayer, modifier: Modifier = Modifier) {
    var isPlaying by remember { mutableStateOf(player.isPlaying) }
    var playbackPosition by remember { mutableStateOf(player.currentPosition) }
    var totalDuration by remember { mutableStateOf(player.duration) }

    LaunchedEffect(player) {
        val listener = object : androidx.media3.common.Player.Listener {
            override fun onIsPlayingChanged(isPlayingValue: Boolean) {
                isPlaying = isPlayingValue
            }

            override fun onPositionDiscontinuity(
                oldPosition: androidx.media3.common.Player.PositionInfo,
                newPosition: androidx.media3.common.Player.PositionInfo,
                reason: Int
            ) {
                playbackPosition = newPosition.positionMs
            }
        }
        player.addListener(listener)

        while (true) {
            playbackPosition = player.currentPosition
            totalDuration = player.duration
            delay(1000)
        }
    }

    Column(modifier = modifier) {
        Slider(
            value = playbackPosition.toFloat(),
            onValueChange = { player.seekTo(it.toLong()) },
            valueRange = 0f..totalDuration.toFloat().coerceAtLeast(0f)
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(durationToString(playbackPosition))
            IconButton(onClick = { player.seekBack() }) {
                Icon(
                    imageVector = Icons.Default.FastRewind,
                    contentDescription = "Rewind"
                )
            }
            IconButton(onClick = { if (isPlaying) player.pause() else player.play() }) {
                Icon(
                    imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                    contentDescription = if (isPlaying) "Pause" else "Play"
                )
            }
            IconButton(onClick = { player.seekForward() }) {
                Icon(
                    imageVector = Icons.Default.FastForward,
                    contentDescription = "Forward"
                )
            }
            Text(durationToString(totalDuration))
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            var showSpeedMenu by remember { mutableStateOf(false) }
            IconButton(onClick = { showSpeedMenu = true }) {
                Icon(
                    imageVector = Icons.Default.Speed,
                    contentDescription = "Playback Speed"
                )
                DropdownMenu(
                    expanded = showSpeedMenu,
                    onDismissRequest = { showSpeedMenu = false }
                ) {
                    DropdownMenuItem(
                        text = { Text("0.5x") },
                        onClick = {
                            player.setPlaybackSpeed(0.5f)
                            showSpeedMenu = false
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("1.0x") },
                        onClick = {
                            player.setPlaybackSpeed(1.0f)
                            showSpeedMenu = false
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("1.5x") },
                        onClick = {
                            player.setPlaybackSpeed(1.5f)
                            showSpeedMenu = false
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("2.0x") },
                        onClick = {
                            player.setPlaybackSpeed(2.0f)
                            showSpeedMenu = false
                        }
                    )
                }
            }
        }
    }
}

private fun durationToString(duration: Long): String {
    return String.format(
        "%02d:%02d",
        TimeUnit.MILLISECONDS.toMinutes(duration),
        TimeUnit.MILLISECONDS.toSeconds(duration) % TimeUnit.MINUTES.toSeconds(1)
    )
}

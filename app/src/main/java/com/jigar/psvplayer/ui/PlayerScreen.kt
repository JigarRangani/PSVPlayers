package com.jigar.psvplayer.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.ui.PlayerView
import com.jigar.psvplayer.player.PlayerViewModel

@Composable
fun PlayerScreen(viewModel: PlayerViewModel) {
    val context = LocalContext.current

    Box(modifier = Modifier.fillMaxSize()) {
        AndroidView(
            factory = {
                PlayerView(context).apply {
                    player = viewModel.exoPlayer
                    useController = false
                }
            },
            modifier = Modifier.fillMaxSize()
        )
        PlayerControls(
            player = viewModel.exoPlayer,
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}

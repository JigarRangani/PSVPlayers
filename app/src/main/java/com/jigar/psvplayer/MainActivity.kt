package com.jigar.psvplayer

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.content.ContextCompat
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.jigar.psvplayer.data.VideoRepository
import com.jigar.psvplayer.player.PlayerViewModel
import com.jigar.psvplayer.ui.PermissionScreen
import com.jigar.psvplayer.ui.PlayerScreen
import com.jigar.psvplayer.ui.VideoListScreen
import com.jigar.psvplayer.ui.theme.PSVPlayerTheme
import com.jigar.psvplayer.viewmodels.VideoListViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PSVPlayerTheme {
                var permissionGranted by remember {
                    mutableStateOf(
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                            ContextCompat.checkSelfPermission(
                                this,
                                Manifest.permission.READ_MEDIA_VIDEO
                            ) == PackageManager.PERMISSION_GRANTED
                        } else {
                            ContextCompat.checkSelfPermission(
                                this,
                                Manifest.permission.READ_EXTERNAL_STORAGE
                            ) == PackageManager.PERMISSION_GRANTED
                        }
                    )
                }

                if (permissionGranted) {
                    val navController = rememberNavController()
                    NavHost(navController = navController, startDestination = "videoList") {
                        composable("videoList") {
                            val viewModel: VideoListViewModel = hiltViewModel()
                            VideoListScreen(
                                viewModel = viewModel,
                                onVideoClick = { video ->
                                    val encodedUri = URLEncoder.encode(video.uri.toString(), StandardCharsets.UTF_8.toString())
                                    navController.navigate("player/$encodedUri")
                                }
                            )
                        }
                        composable(
                            "player/{videoUri}",
                            arguments = listOf(navArgument("videoUri") { type = NavType.StringType })
                        ) {
                            val viewModel: PlayerViewModel = hiltViewModel()
                            PlayerScreen(viewModel = viewModel)
                        }
                    }
                } else {
                    PermissionScreen {
                        permissionGranted = true
                    }
                }
            }
        }
    }
}
package com.jigar.psvplayer

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
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
import com.jigar.psvplayer.viewmodels.VideoListViewModelFactory

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PSVPlayerTheme {
                var permissionGranted by remember { mutableStateOf(false) }

                if (permissionGranted) {
                    val navController = rememberNavController()
                    NavHost(navController = navController, startDestination = "videoList") {
                        composable("videoList") {
                            val viewModel: VideoListViewModel = viewModel(
                                factory = VideoListViewModelFactory(VideoRepository(applicationContext))
                            )
                            VideoListScreen(
                                viewModel = viewModel,
                                onVideoClick = { video ->
                                    navController.navigate("player/${video.uri}")
                                }
                            )
                        }
                        composable(
                            "player/{videoUri}",
                            arguments = listOf(navArgument("videoUri") { type = NavType.StringType })
                        ) { backStackEntry ->
                            val viewModel: PlayerViewModel = viewModel(
                                factory = PlayerViewModelFactory(applicationContext),
                                viewModelStoreOwner = backStackEntry
                            )
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
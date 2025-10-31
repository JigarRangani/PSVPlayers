package com.jigar.psvplayer

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.jigar.psvplayer.player.PlayerViewModel
import com.jigar.psvplayer.ui.FolderListScreen
import com.jigar.psvplayer.ui.PermissionScreen
import com.jigar.psvplayer.ui.PlayerScreen
import com.jigar.psvplayer.ui.VideoListScreen
import com.jigar.psvplayer.ui.theme.PSVPlayerTheme
import com.jigar.psvplayer.viewmodels.FolderListViewModel
import com.jigar.psvplayer.viewmodels.VideoListViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContent {
            PSVPlayerTheme {
                val permissionGranted =
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

                if (permissionGranted) {
                    val navController = rememberNavController()
                    NavHost(navController = navController, startDestination = "folderList") {
                        composable("folderList") {
                            val viewModel: FolderListViewModel = hiltViewModel()
                            FolderListScreen(
                                viewModel = viewModel,
                                onFolderClick = { bucketId ->
                                    navController.navigate("videoList/$bucketId")
                                }
                            )
                        }
                        composable(
                            "videoList/{bucketId}",
                            arguments = listOf(navArgument("bucketId") {
                                type = NavType.StringType
                            })
                        ) {
                            val viewModel: VideoListViewModel = hiltViewModel()
                            VideoListScreen(
                                viewModel = viewModel,
                                onVideoClick = { video ->
                                    val encodedUri = URLEncoder.encode(
                                        video.uri.toString(),
                                        StandardCharsets.UTF_8.toString()
                                    )
                                    navController.navigate("player/$encodedUri")
                                }
                            )
                        }
                        composable(
                            "player/{videoUri}",
                            arguments = listOf(navArgument("videoUri") {
                                type = NavType.StringType
                            })
                        ) {
                            val viewModel: PlayerViewModel = hiltViewModel()
                            PlayerScreen(viewModel = viewModel)
                        }
                    }
                } else {
                    PermissionScreen {
                        recreate()
                    }
                }
            }
        }
    }
}
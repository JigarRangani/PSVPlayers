package com.jigar.psvplayer.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.jigar.psvplayer.data.VideoFolder
import com.jigar.psvplayer.viewmodels.FolderListViewModel

@Composable
fun FolderListScreen(
    viewModel: FolderListViewModel,
    onFolderClick: (String) -> Unit
) {
    val folders by viewModel.folders.collectAsState()

    AppScaffold(title = "Video Folders") { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(horizontal = 16.dp)
        ) {
            items(folders) { folder ->
                FolderListItem(
                    folder = folder,
                    onFolderClick = { onFolderClick(folder.bucketId) }
                )
            }
        }
    }
}

@Composable
fun FolderListItem(
    folder: VideoFolder,
    onFolderClick: () -> Unit
) {
    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable { onFolderClick() }
    ) {
        Row(
            modifier = Modifier.padding(8.dp)
        ) {
            AsyncImage(
                model = folder.firstVideoUri,
                contentDescription = "Folder thumbnail",
                modifier = Modifier
                    .width(120.dp)
                    .height(70.dp),
                contentScale = ContentScale.Crop
            )
            Column(
                modifier = Modifier
                    .padding(start = 8.dp)
                    .weight(1f)
            ) {
                Text(
                    text = folder.folderName,
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = "${folder.videoCount} videos",
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}
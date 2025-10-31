package com.jigar.psvplayer.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jigar.psvplayer.data.VideoItem
import com.jigar.psvplayer.data.VideoRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class VideoListUiState {
    object Loading : VideoListUiState()
    data class Success(val videos: List<VideoItem>) : VideoListUiState()
    object Empty : VideoListUiState()
}

class VideoListViewModel(private val repository: VideoRepository) : ViewModel() {

    private val _uiState = MutableStateFlow<VideoListUiState>(VideoListUiState.Loading)
    val uiState: StateFlow<VideoListUiState> = _uiState

    fun loadVideos() {
        viewModelScope.launch {
            _uiState.value = VideoListUiState.Loading
            val videos = repository.getVideos()
            _uiState.value = if (videos.isEmpty()) {
                VideoListUiState.Empty
            } else {
                VideoListUiState.Success(videos)
            }
        }
    }

    fun deleteVideo(videoItem: VideoItem) {
        viewModelScope.launch {
            repository.deleteVideo(videoItem)
            loadVideos()
        }
    }
}

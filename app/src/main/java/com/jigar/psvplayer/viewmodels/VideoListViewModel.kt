package com.jigar.psvplayer.viewmodels

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jigar.psvplayer.data.VideoItem
import com.jigar.psvplayer.data.VideoRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class VideoListViewModel @Inject constructor(
    private val repository: VideoRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val bucketId = savedStateHandle.get<String>("bucketId")!!

    val videos: StateFlow<List<VideoItem>> = repository.getVideosInFolder(bucketId)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun deleteVideo(videoItem: VideoItem) {
        viewModelScope.launch {
            repository.deleteVideo(videoItem)
        }
    }
}
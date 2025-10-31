package com.jigar.psvplayer.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jigar.psvplayer.data.VideoFolder
import com.jigar.psvplayer.data.VideoRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class FolderListViewModel @Inject constructor(
    private val repository: VideoRepository
) : ViewModel() {

    val folders: StateFlow<List<VideoFolder>> = repository.getFolderList()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
}
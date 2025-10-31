package com.jigar.psvplayer.player

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "playback_positions")

class PlayerViewModel(
    private val savedStateHandle: SavedStateHandle,
    private val context: Context
) : ViewModel() {

    val exoPlayer: ExoPlayer = ExoPlayer.Builder(context).build()
    private val videoUri by lazy { savedStateHandle.get<String>("videoUri") }

    init {
        viewModelScope.launch {
            val playbackPosition = getPlaybackPosition(videoUri ?: "")
            videoUri?.let {
                exoPlayer.setMediaItem(MediaItem.fromUri(it))
                exoPlayer.prepare()
                exoPlayer.seekTo(playbackPosition)
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        viewModelScope.launch {
            savePlaybackPosition(videoUri ?: "", exoPlayer.currentPosition)
            exoPlayer.release()
        }
    }

    private suspend fun savePlaybackPosition(uri: String, position: Long) {
        context.dataStore.edit { preferences ->
            preferences[longPreferencesKey(uri)] = position
        }
    }

    private suspend fun getPlaybackPosition(uri: String): Long {
        return context.dataStore.data
            .map { preferences ->
                preferences[longPreferencesKey(uri)] ?: 0L
            }.first()
    }
}

package com.practicum.playlistmaker.media_library.ui

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.search.domain.db.PlaylistInteractor
import com.practicum.playlistmaker.search.domain.models.Playlist
import kotlinx.coroutines.launch

class PlaylistsViewModel(
    private val playlistInteractor: PlaylistInteractor,
): ViewModel() {
    private val playlistsLiveData = MutableLiveData<List<Playlist>>(emptyList())
    fun observePlaylists(): LiveData<List<Playlist>> = playlistsLiveData

    fun loadPlaylists() {
        viewModelScope.launch {
            playlistInteractor.getPlaylists().collect {
                playlistsLiveData.postValue(it)
            }
        }
    }
}
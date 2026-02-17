package com.practicum.playlistmaker.media_library.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class PlaylistsViewModel: ViewModel() {
    private val playlistsLiveData = MutableLiveData("")
    fun observePlaylists(): LiveData<String> = playlistsLiveData
}
package com.practicum.playlistmaker.media_library.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class FavoritesViewModel: ViewModel() {
    private val favoritesLiveData = MutableLiveData("")
    fun observeFavorites(): LiveData<String> = favoritesLiveData
}
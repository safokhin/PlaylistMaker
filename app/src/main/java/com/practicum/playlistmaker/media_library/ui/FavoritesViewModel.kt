package com.practicum.playlistmaker.media_library.ui

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.search.domain.api.TracksHistoryInteractor
import com.practicum.playlistmaker.search.domain.db.FavoritesInteractor
import com.practicum.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.launch

class FavoritesViewModel(
    private val favoritesInteractor: FavoritesInteractor,
    private val tracksHistoryInteractor: TracksHistoryInteractor
): ViewModel() {
    private val favoritesLiveData = MutableLiveData<List<Track>>(emptyList())
    fun observeFavorites(): LiveData<List<Track>> = favoritesLiveData

    fun loadFavoritesTracks() {
        viewModelScope.launch {
            favoritesInteractor.favoritesTracks().collect {
                favoritesLiveData.postValue(it)
            }
        }
    }

    /** Добавление трека в историю */
    fun addHistory(track: Track) {
        tracksHistoryInteractor.addHistory(track)
    }
}
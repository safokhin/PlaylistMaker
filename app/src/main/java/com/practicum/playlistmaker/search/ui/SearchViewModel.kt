package com.practicum.playlistmaker.search.ui

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.search.domain.api.TracksHistoryInteractor
import com.practicum.playlistmaker.search.domain.api.TracksSearchInteractor
import com.practicum.playlistmaker.search.domain.models.SearchActivityState
import com.practicum.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SearchViewModel(
    private val tracksSearchInteractor: TracksSearchInteractor,
    private val tracksHistoryInteractor: TracksHistoryInteractor
): ViewModel() {
    companion object {
        private const val SEARCH_DEBOUNCE_DELAY = 2000L
    }

    private val activityStateLiveData = MutableLiveData<SearchActivityState>()
    fun observeSearchActivity(): LiveData<SearchActivityState> = activityStateLiveData
    private var latestSearchQuery: String? = null

    private var searchJob: Job? = null

    /** Отображение истории */
    fun loadHistory() {
        val historyTracks = tracksHistoryInteractor.getHistory()

        if(historyTracks.isEmpty()) {
            activityStateLiveData.postValue(SearchActivityState.Content(emptyList()))
        } else {
            activityStateLiveData.postValue(SearchActivityState.History(historyTracks))
        }
    }

    /** Добавление трека в историю */
    fun addHistory(track: Track) {
        tracksHistoryInteractor.addHistory(track)
    }

    /** Очистка истории */
    fun clearHistory() {
        tracksHistoryInteractor.clearHistory()
        activityStateLiveData.postValue(SearchActivityState.Content(emptyList()))
    }

    fun searchTrackDebounce(query: String, isError: Boolean = false) {
        if(query.isEmpty()) {
            latestSearchQuery = ""
            return
        }

        // Если произошла ошибка, то строки равны
        if(latestSearchQuery == query && !isError) return
        latestSearchQuery = query

        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            delay(SEARCH_DEBOUNCE_DELAY)
            searchTracks(query)
        }
    }

    private fun searchTracks(query: String) {
        activityStateLiveData.postValue(SearchActivityState.Loading)

        viewModelScope.launch {
            tracksSearchInteractor
                .searchTracks(query)
                .collect { result ->
                    result.onSuccess { tracks ->
                        if(tracks.isEmpty()) {
                            activityStateLiveData.postValue(SearchActivityState.Empty)
                        } else {
                            activityStateLiveData.postValue(SearchActivityState.Content(tracks))
                        }
                    }
                    result.onFailure {
                        activityStateLiveData.postValue(SearchActivityState.Error)
                    }
                }
        }
    }
}
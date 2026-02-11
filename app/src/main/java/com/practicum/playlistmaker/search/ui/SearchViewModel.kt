package com.practicum.playlistmaker.search.ui

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.practicum.playlistmaker.PlaylistMakerApp
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import com.practicum.playlistmaker.creator.Creator
import com.practicum.playlistmaker.search.domain.models.SearchActivityState
import com.practicum.playlistmaker.search.domain.models.Track

class SearchViewModel(private val context: Context): ViewModel() {
    companion object {
        fun getFactory(): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val app = (this[APPLICATION_KEY] as PlaylistMakerApp)
                SearchViewModel(app)
            }
        }

        private const val SEARCH_DEBOUNCE_DELAY = 2000L
        private val TOKEN_SEARCH = Any()
    }

    private val activityStateLiveData = MutableLiveData<SearchActivityState>()
    fun observeSearchActivity(): LiveData<SearchActivityState> = activityStateLiveData

    private var tracksSearchInteractor = Creator.provideTracksSearchInteractor()
    private var tracksHistoryInteractor = Creator.provideTracksHistoryInteractor(context)

    private val handler = Handler(Looper.getMainLooper())
    private var latestSearchQuery: String? = null

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
        // Очищаем сразу предыдущий запрос
        handler.removeCallbacksAndMessages(TOKEN_SEARCH)

        if(query.isEmpty()) {
            latestSearchQuery = ""
            return
        }

        // Если произошла ошибка, то строки равны
        if(latestSearchQuery == query && !isError) return
        latestSearchQuery = query

        val searchRunnable = Runnable { searchTracks(query) }
        val postTime = SystemClock.uptimeMillis() + SEARCH_DEBOUNCE_DELAY
        handler.postAtTime(searchRunnable, TOKEN_SEARCH, postTime)
    }

    private fun searchTracks(query: String) {
        activityStateLiveData.postValue(SearchActivityState.Loading)

        tracksSearchInteractor.searchTracks(query) { result ->

            result.onSuccess { tracks ->
                if(tracks.isEmpty()) {
                    activityStateLiveData.postValue(SearchActivityState.Empty)
                } else {
                    activityStateLiveData.postValue(SearchActivityState.Content(tracks))
                }
            }.onFailure {
                activityStateLiveData.postValue(SearchActivityState.Error)
            }
        }
    }
}
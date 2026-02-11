package com.practicum.playlistmaker.search.domain.api

import com.practicum.playlistmaker.search.domain.models.Track

interface TracksHistoryInteractor {
    fun addHistory(track: Track)
    fun getHistory(): List<Track>
    fun clearHistory()
}
package com.practicum.playlistmaker.domain.api

import com.practicum.playlistmaker.domain.models.Track

interface TracksHistoryInteractor {
    fun addHistory(track: Track)
    fun getHistory(): List<Track>
    fun clearHistory()
}
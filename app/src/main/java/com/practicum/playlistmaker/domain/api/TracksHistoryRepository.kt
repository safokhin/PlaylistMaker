package com.practicum.playlistmaker.domain.api

import com.practicum.playlistmaker.domain.models.Track

interface TracksHistoryRepository {
    fun addHistory(track: Track)
    fun getHistory(): List<Track>
    fun clearHistory()
}
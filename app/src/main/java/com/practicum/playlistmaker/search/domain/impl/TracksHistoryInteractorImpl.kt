package com.practicum.playlistmaker.search.domain.impl

import com.practicum.playlistmaker.search.domain.api.TracksHistoryInteractor
import com.practicum.playlistmaker.search.domain.api.TracksHistoryRepository
import com.practicum.playlistmaker.search.domain.models.Track

class TracksHistoryInteractorImpl(
    private val repository: TracksHistoryRepository
): TracksHistoryInteractor {
    override fun addHistory(track: Track) {
        repository.addHistory(track)
    }

    override fun getHistory(): List<Track> {
        return repository.getHistory()
    }

    override fun clearHistory() {
        repository.clearHistory()
    }
}
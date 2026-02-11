package com.practicum.playlistmaker.search.domain.impl

import com.practicum.playlistmaker.search.domain.api.TracksSearchInteractor
import com.practicum.playlistmaker.search.domain.api.TracksSearchRepository
import com.practicum.playlistmaker.search.domain.models.Track

class TracksSearchInteractorImpl(
    private val repository: TracksSearchRepository
): TracksSearchInteractor {

    override fun searchTracks(expression: String, callback: (Result<List<Track>>) -> Unit) {
        repository.searchTracks(expression, callback)
    }
}
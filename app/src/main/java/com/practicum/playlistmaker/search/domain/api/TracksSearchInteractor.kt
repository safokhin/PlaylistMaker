package com.practicum.playlistmaker.search.domain.api

import com.practicum.playlistmaker.search.domain.models.Track

/** Это интерфейс, с помощью которого слой Presentation будет общаться со слоем Domain. */
interface TracksSearchInteractor {
    fun searchTracks(expression: String, callback: (Result<List<Track>>) -> Unit)
}
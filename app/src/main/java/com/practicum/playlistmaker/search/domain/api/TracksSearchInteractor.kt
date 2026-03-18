package com.practicum.playlistmaker.search.domain.api

import com.practicum.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow

/** Это интерфейс, с помощью которого слой Presentation будет общаться со слоем Domain. */
interface TracksSearchInteractor {
    fun searchTracks(expression: String): Flow<Result<List<Track>>>
}
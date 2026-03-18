package com.practicum.playlistmaker.search.domain.api

import com.practicum.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow


/** Это интерфейс для связи слоя Domain со слоем Data */
interface TracksSearchRepository {
    /** Поиск треков */
    fun searchTracks(expression: String): Flow<Result<List<Track>>>
}
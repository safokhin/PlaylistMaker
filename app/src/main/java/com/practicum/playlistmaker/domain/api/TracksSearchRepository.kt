package com.practicum.playlistmaker.domain.api

import com.practicum.playlistmaker.domain.models.Track


/** Это интерфейс для связи слоя Domain со слоем Data */
interface TracksSearchRepository {
    /** Поиск треков */
    fun searchTracks(expression: String, callback: (Result<List<Track>>) -> Unit)
}
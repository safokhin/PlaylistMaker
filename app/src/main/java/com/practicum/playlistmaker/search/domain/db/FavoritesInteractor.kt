package com.practicum.playlistmaker.search.domain.db

import com.practicum.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow

interface FavoritesInteractor {

    suspend fun favoriteTrackById(id: String): Flow<Track?>
    suspend fun favoritesTracks(): Flow<List<Track>>

    suspend fun addTrack(track: Track)

    suspend fun removeTrack(track: Track)
}

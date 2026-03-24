package com.practicum.playlistmaker.search.domain.impl

import com.practicum.playlistmaker.search.domain.db.FavoritesInteractor
import com.practicum.playlistmaker.search.domain.db.FavoritesRepository
import com.practicum.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow

class FavoritesInteractorImpl(
    private val repository: FavoritesRepository
): FavoritesInteractor {
    override suspend fun favoriteTrackById(id: String): Flow<Track?> {
        return repository.favoriteTrackById(id)
    }


    override suspend fun favoritesTracks(): Flow<List<Track>> {
        return repository.favoritesTracks()
    }

    override suspend fun addTrack(track: Track) {
        repository.addTrack(track)
    }

    override suspend fun removeTrack(track: Track) {
        repository.removeTrack(track)
    }
}
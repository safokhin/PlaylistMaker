package com.practicum.playlistmaker.search.data.repository

import android.util.Log
import com.practicum.playlistmaker.search.data.converters.TrackDbConvertor
import com.practicum.playlistmaker.search.data.db.AppDatabase
import com.practicum.playlistmaker.search.data.db.entity.TrackEntity
import com.practicum.playlistmaker.search.domain.db.FavoritesRepository
import com.practicum.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class FavoritesRepositoryImpl(
    private val appDatabase: AppDatabase,
    private val trackDbConvertor: TrackDbConvertor
): FavoritesRepository {
    override suspend fun favoriteTrackById(id: String): Flow<Track?> = flow {
        val track = appDatabase.trackDao().getTrackById(id)
        emit(track?.let{ trackDbConvertor.map(it) })
    }


    override suspend fun favoritesTracks(): Flow<List<Track>> = flow {
        val tracks = appDatabase.trackDao().getTracks()
        emit(convertFromTrackEntity(tracks))
    }

    override suspend fun addTrack(track: Track) {
        appDatabase.trackDao().insertTrack(convertToTrackEntity(listOf(track)))
    }

    override suspend fun removeTrack(track: Track) {
        appDatabase.trackDao().removeTrack(convertToTrackEntity(listOf(track)))
    }

    private fun convertFromTrackEntity(tracks: List<TrackEntity>): List<Track> {
        return tracks.map { track -> trackDbConvertor.map(track) }
    }

    private fun convertToTrackEntity(tracks: List<Track>): List<TrackEntity> {
        return tracks.map { track -> trackDbConvertor.map(track) }
    }
}


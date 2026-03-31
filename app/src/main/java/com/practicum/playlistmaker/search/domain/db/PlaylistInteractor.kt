package com.practicum.playlistmaker.search.domain.db

import com.practicum.playlistmaker.search.domain.models.Playlist
import com.practicum.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow

interface PlaylistInteractor {
    // Создание/редактирование
    suspend fun insertPlaylist(playlist: Playlist)

    suspend fun removePlaylist(id: String)

    suspend fun getPlaylists(): Flow<List<Playlist>>

    suspend fun addTrack(track: Track, playlist: Playlist)
}
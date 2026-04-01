package com.practicum.playlistmaker.search.domain.impl

import android.net.Uri
import android.util.Log
import com.practicum.playlistmaker.search.domain.db.PlaylistInteractor
import com.practicum.playlistmaker.search.domain.db.PlaylistRepository
import com.practicum.playlistmaker.search.domain.models.Playlist
import com.practicum.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow

class PlaylistInteractorImpl(
    private val repository: PlaylistRepository
): PlaylistInteractor {
    override suspend fun insertPlaylist(playlist: Playlist) {
        repository.insertPlaylist(playlist)
    }

    override suspend fun removePlaylist(id: String) {
        repository.removePlaylist(id)
    }

    override suspend fun getPlaylists(): Flow<List<Playlist>> {
        return repository.getPlaylists()
    }

    override suspend fun getPlaylistById(id: String): Playlist {
        return repository.getPlaylistById(id)
    }

    override suspend fun addTrack(
        track: Track,
        playlist: Playlist
    ) {
        repository.addTrack(track, playlist)
    }

    override suspend fun getTracks(ids: List<String>): List<Track> {
        return repository.getTracks(ids)
    }

    override suspend fun removeTrackInPlaylist(trackId: Long, playlistId: Long) {
        repository.removeTrackInPlaylist(trackId, playlistId)
    }
}
package com.practicum.playlistmaker.search.data.repository

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Environment
import android.util.Log
import com.practicum.playlistmaker.search.data.converters.PlaylistDbConvertor
import com.practicum.playlistmaker.search.data.converters.TrackPlaylistDbConvertor
import com.practicum.playlistmaker.search.data.db.AppDatabase
import com.practicum.playlistmaker.search.data.db.entity.PlaylistEntity
import com.practicum.playlistmaker.search.data.db.entity.TrackPlaylistEntity
import com.practicum.playlistmaker.search.domain.db.PlaylistRepository
import com.practicum.playlistmaker.search.domain.models.Playlist
import com.practicum.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.io.File
import java.io.FileOutputStream

class PlaylistRepositoryImpl(
    private val appDatabase: AppDatabase,
    private val playlistDbConvertor: PlaylistDbConvertor,
    private val trackPlaylistDbConvertor: TrackPlaylistDbConvertor,
): PlaylistRepository {
    override suspend fun insertPlaylist(playlist: Playlist) {
        appDatabase.playlistDao().insertPlaylist(convertToPlaylistEntity(playlist))
    }

    override suspend fun removePlaylist(id: String) {
        appDatabase.playlistDao().removePlaylist(id)
    }

    override suspend fun getPlaylists(): Flow<List<Playlist>> = flow {
        val playlists = appDatabase.playlistDao().getPlaylists()
        emit(playlists.map { playlist -> playlistDbConvertor.map(playlist) })
    }

    override suspend fun getPlaylistById(id: String): Playlist {
       val playlist = appDatabase.playlistDao().getPlaylistById(id)
        return playlistDbConvertor.map(playlist)
    }

    override suspend fun addTrack(
        track: Track,
        playlist: Playlist
    ) {
        val playlistClone = playlist.copy(
            tracksId = playlist.tracksId + track.trackId
        )

        appDatabase.trackPlaylistDao().insertTrack(convertToTrackPlaylistEntity(track))
        appDatabase.playlistDao().insertPlaylist(convertToPlaylistEntity(playlistClone))
    }

    override suspend fun getTracks(ids: List<String>): List<Track> {
        return appDatabase.trackPlaylistDao().getTracks()
            .filter { track -> ids.contains(track.trackId.toString()) }
            .map { item -> trackPlaylistDbConvertor.map(item) }
    }

    override suspend fun removeTrackInPlaylist(trackId: Long, playlistId: Long) {
        val playlist = appDatabase.playlistDao().getPlaylistById(playlistId.toString())
        val tracksId = playlist.tracksId.filter { it != trackId }

        appDatabase.playlistDao().updatePlaylist(playlist.copy(tracksId = tracksId))

        val tracksListId = appDatabase.trackPlaylistDao().getTracks().map { it.trackId }
        if (!tracksListId.contains(trackId)) {
            appDatabase.trackPlaylistDao().removeTrack(trackId.toString())
        }
    }

    private fun convertToPlaylistEntity(playlist: Playlist): PlaylistEntity {
        return playlistDbConvertor.map(playlist)
    }

    private fun convertToTrackPlaylistEntity(track: Track): TrackPlaylistEntity {
        return trackPlaylistDbConvertor.map(track)
    }
}
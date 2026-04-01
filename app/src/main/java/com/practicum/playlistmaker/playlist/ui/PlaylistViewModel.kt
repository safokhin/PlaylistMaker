package com.practicum.playlistmaker.playlist.ui

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.search.domain.db.PlaylistInteractor
import com.practicum.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.launch

class PlaylistViewModel(
    private val playlistInteractor: PlaylistInteractor,
): ViewModel() {
    private val playlistLiveData = MutableLiveData<CurrentPlaylistState>()
    fun observePlaylist(): LiveData<CurrentPlaylistState> = playlistLiveData

    fun loadPlaylist(id: Long) {
        viewModelScope.launch {
            val playlistData = playlistInteractor.getPlaylistById(id.toString())
            val tracks = playlistInteractor.getTracks(playlistData.tracksId.map { item -> item.toString() })
            val duration = getDuration(tracks)


            playlistLiveData.postValue(CurrentPlaylistState(
                id = playlistData.id,
                name = playlistData.name,
                description = playlistData.description ?: "",
                uri = playlistData.uri,
                tracks = tracks,
                durationMinutes = duration
            ))
        }
    }

    fun getPlaylist(): CurrentPlaylistState {
        return playlistLiveData.value!!
    }

    fun removeTrack(track: Track) {
        viewModelScope.launch {
            val tracks = playlistLiveData.value?.tracks?.filter { it.trackId != track.trackId } ?: emptyList()
            val duration = getDuration(tracks)

            playlistInteractor.removeTrackInPlaylist(track.trackId, playlistLiveData.value?.id!!)
            playlistLiveData.postValue(playlistLiveData.value?.copy(tracks = tracks, durationMinutes = duration))
        }
    }

    fun removePlaylist() {
        viewModelScope.launch {
            playlistInteractor.removePlaylist(playlistLiveData.value?.id.toString())
        }
    }

    /** Подсчет минут (секунды обрезаем) */
    private fun getDuration(tracks: List<Track>): Int {
        var duration = 0

        tracks.forEach { track ->
            val arr = track.trackTime.split(":")
            duration += arr[0].toInt() * 60 + arr[1].toInt()
        }

        return duration / 60
    }

}
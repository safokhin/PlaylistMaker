package com.practicum.playlistmaker.player.ui

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.search.domain.db.PlaylistInteractor
import com.practicum.playlistmaker.search.domain.models.Playlist
import com.practicum.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.launch

class PlaylistBSViewModel(
    private val playlistInteractor: PlaylistInteractor,
): ViewModel() {
    private val playlistBSLiveData = MutableLiveData<PlaylistBSState>()
    fun observePlaylistBSLiveData(): LiveData<PlaylistBSState> = playlistBSLiveData

    fun addTrackInPlaylist(track: Track, playlist: Playlist) {
        viewModelScope.launch {
            val isContainsTrack = playlist.tracksId.contains(track.trackId)

            if (!isContainsTrack) {
                playlistBSLiveData.postValue(PlaylistBSState.TrackStatus(TrackStatusState.Added(playlist)))
                playlistInteractor.addTrack(track, playlist)
                loadPlaylists()
            } else {
                playlistBSLiveData.postValue(PlaylistBSState.TrackStatus(TrackStatusState.AlreadyExists(playlist)))
            }
        }
    }

    fun loadPlaylists() {
        viewModelScope.launch {
            playlistInteractor.getPlaylists().collect {
                playlistBSLiveData.postValue(PlaylistBSState.Content(it))
            }
        }
    }
}
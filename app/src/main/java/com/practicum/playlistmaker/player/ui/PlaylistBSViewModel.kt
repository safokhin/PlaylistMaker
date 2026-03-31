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

    fun addTrackInPlaylist(track: Track, playlist: Playlist): Boolean {
        val isContainsTrack = playlist.tracksId.contains(track.trackId)

        viewModelScope.launch {

            if (!isContainsTrack) {
                playlistInteractor.addTrack(track, playlist)
                loadPlaylists()
            }
        }

        return isContainsTrack
    }

    fun loadPlaylists() {
        viewModelScope.launch {
            playlistInteractor.getPlaylists().collect {
                playlistBSLiveData.postValue(PlaylistBSState.Content(it))
            }
        }
    }
}
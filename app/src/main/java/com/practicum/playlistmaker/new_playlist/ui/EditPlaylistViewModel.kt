package com.practicum.playlistmaker.new_playlist.ui

import android.net.Uri
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.search.domain.db.PlaylistInteractor
import kotlinx.coroutines.launch
import java.io.File

class EditPlaylistViewModel(
    playlistInteractor: PlaylistInteractor
): NewPlaylistViewModel(playlistInteractor) {

    fun loadPlaylistInfo(id: Long) {
        viewModelScope.launch {
            val playlist = playlistInteractor.getPlaylistById(id.toString())
            val uri = playlist.uri
                ?.takeIf { it.isNotEmpty() }
                ?.let { Uri.fromFile(File(it)) }

            playlistLiveData.postValue(
                PlaylistState(
                    name = playlist.name,
                    description = playlist.description ?: "",
                    uri = uri
                )
            )
        }
    }

    fun editPlaylist(id: Long, absolutePath: String) {
        viewModelScope.launch {
            val playlist = playlistInteractor.getPlaylistById(id.toString())

            playlistInteractor.insertPlaylist(playlist.copy(
                name = playlistLiveData.value?.name ?: "",
                description = playlistLiveData.value?.description ?: "",
                uri = absolutePath
            ))
        }
    }
}
package com.practicum.playlistmaker.new_playlist.ui

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Environment
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.search.domain.db.PlaylistInteractor
import com.practicum.playlistmaker.search.domain.models.Playlist
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream

open class NewPlaylistViewModel(
    protected val playlistInteractor: PlaylistInteractor
): ViewModel() {
    protected val playlistLiveData = MutableLiveData(PlaylistState())
    open fun observePlaylist(): LiveData<PlaylistState> = playlistLiveData

    open fun getName(): String {
        return playlistLiveData.value?.name ?: ""
    }

    open fun getUri(): Uri? {
        return playlistLiveData.value?.uri
    }

    open fun changeName(name: String) {
        playlistLiveData.value = playlistLiveData.value?.copy(name = name)
    }

    open fun changeDescription(description: String) {
        playlistLiveData.value = playlistLiveData.value?.copy(description = description)
    }

    open fun changeImg(uri: Uri) {
        playlistLiveData.value = playlistLiveData.value?.copy(uri = uri)
    }

    open fun isStartCreating(): Boolean {
        return !playlistLiveData.value?.name?.isEmpty()!! ||
                !playlistLiveData.value?.description?.isEmpty()!! ||
                playlistLiveData.value?.uri != null
    }

    open fun createPlaylist(absolutePath: String) {
        viewModelScope.launch {
            playlistInteractor.insertPlaylist(Playlist(
                name = playlistLiveData.value?.name ?: "",
                description = playlistLiveData.value?.description,
                uri = absolutePath,
                tracksId = emptyList()
            ))
        }
    }
}
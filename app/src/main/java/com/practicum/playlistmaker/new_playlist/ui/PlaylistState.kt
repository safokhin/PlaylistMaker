package com.practicum.playlistmaker.new_playlist.ui

import android.net.Uri

data class PlaylistState(
    val name: String = "",
    val description: String = "",
    val uri: Uri? = null
)
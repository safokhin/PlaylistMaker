package com.practicum.playlistmaker.player.ui

import com.practicum.playlistmaker.search.domain.models.Playlist

sealed interface PlaylistBSState {
    data class Content(val playlists: List<Playlist>): PlaylistBSState
}
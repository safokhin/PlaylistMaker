package com.practicum.playlistmaker.player.ui

import com.practicum.playlistmaker.search.domain.models.Playlist

sealed interface PlaylistBSState {
    data class Content(val playlists: List<Playlist>): PlaylistBSState

    data class TrackStatus(val trackStatus: TrackStatusState): PlaylistBSState
}

sealed interface TrackStatusState {
    data class AlreadyExists(val playlist: Playlist) : TrackStatusState
    data class Added(val playlist: Playlist) : TrackStatusState
}
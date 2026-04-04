package com.practicum.playlistmaker.playlist.ui

import com.practicum.playlistmaker.search.domain.models.Track

data class CurrentPlaylistState(
    val id: Long = 0,
    val name: String,
    val description: String,
    val uri: String?,
    val tracks: List<Track>,
    val durationMinutes: Int,
)
package com.practicum.playlistmaker.search.domain.models

data class Playlist (
    val id: Long = 0,
    val name: String,
    val description: String?,
    val uri: String?,
    val tracksId: List<Long>,
    val tracksCount: Int = 0
)

package com.practicum.playlistmaker.search.domain.models

sealed interface SearchActivityState {
    object Loading : SearchActivityState
    object Empty : SearchActivityState
    object Error : SearchActivityState

    data class Content(val tracks: List<Track>): SearchActivityState
    data class History(val tracks: List<Track>): SearchActivityState
}
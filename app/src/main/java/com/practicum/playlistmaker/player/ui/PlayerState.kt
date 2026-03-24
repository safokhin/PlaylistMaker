package com.practicum.playlistmaker.player.ui

import com.practicum.playlistmaker.search.domain.models.Track

sealed class PlayerState(val isPlay: Boolean, val progressTime: String, val disableButton: Boolean, val track: Track) {
    class Default(track: Track): PlayerState(false, "00:00", true, track) //  начальное состояние (плеер ещё не готов к работе)
    class Prepared(track: Track): PlayerState(false, "00:00", false, track) // плеер готов воспроизводить
    class Playing(progressTime: String, track: Track): PlayerState(true, progressTime, false, track) // воспроизведение идёт
    class Paused(progressTime: String, track: Track): PlayerState(false, progressTime, false, track) // воспроизведение на паузе
}
package com.practicum.playlistmaker.player.ui

sealed class PlayerState(val isPlay: Boolean, val progressTime: String, val disableButton: Boolean) {
    class Default: PlayerState(false, "00:00", true) //  начальное состояние (плеер ещё не готов к работе)
    class Prepared: PlayerState(false, "00:00", false) // плеер готов воспроизводить
    class Playing(progressTime: String): PlayerState(true, progressTime, false) // воспроизведение идёт
    class Paused(progressTime: String): PlayerState(false, progressTime, false) // воспроизведение на паузе
}
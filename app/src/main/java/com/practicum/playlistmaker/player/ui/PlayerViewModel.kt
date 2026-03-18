package com.practicum.playlistmaker.player.ui

import android.media.MediaPlayer
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale

class PlayerViewModel(
    private val track: Track,
    private val mediaPlayer: MediaPlayer
): ViewModel() {
    companion object {
        private const val DELAY_UPDATE_PROGRESS = 300L
    }

    private val playerLiveData = MutableLiveData<PlayerState>(PlayerState.Default())
    fun observePlayer(): LiveData<PlayerState> = playerLiveData
    private var timerJob: Job? = null

    init {
        preparePlayer()
    }

    override fun onCleared() {
        super.onCleared()
        mediaPlayer.release()
        resetTimer()
    }

    fun onPause() {
        pausePlayer()
    }

    /** Поведение плеера */
    fun playbackControl() {
        when (playerLiveData.value) {
            is PlayerState.Playing -> pausePlayer()
            is PlayerState.Prepared, is PlayerState.Paused -> startPlayer()
            else -> null
        }
    }

    private fun preparePlayer() {
        mediaPlayer.setDataSource(track.previewUrl)
        mediaPlayer.prepareAsync()
        // Завершение подготовки
        mediaPlayer.setOnPreparedListener {
            playerLiveData.postValue(PlayerState.Prepared())
        }
        // Завершение воспроизведения
        mediaPlayer.setOnCompletionListener {
            resetTimer()
        }
    }

    private fun startPlayer() {
        mediaPlayer.start()
        startTimerUpdate()
    }

    private fun pausePlayer() {
        timerJob?.cancel()
        mediaPlayer.pause()
        playerLiveData.postValue(PlayerState.Paused(getProgressTime()))
    }

    /** Обновление таймера трека */
    private fun startTimerUpdate() {
        playerLiveData.postValue(PlayerState.Playing(getProgressTime()))
        timerJob = viewModelScope.launch {
            while (mediaPlayer.isPlaying) {
                delay(DELAY_UPDATE_PROGRESS)
                playerLiveData.postValue(PlayerState.Playing(getProgressTime()))
            }
        }

    }

    private fun resetTimer() {
        playerLiveData.postValue(PlayerState.Prepared())
        timerJob?.cancel()
    }

    private fun getProgressTime(): String {
        return SimpleDateFormat("mm:ss", Locale.getDefault()).format(mediaPlayer.currentPosition)
    }
}
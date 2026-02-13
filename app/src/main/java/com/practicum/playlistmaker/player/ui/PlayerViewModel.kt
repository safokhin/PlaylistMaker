package com.practicum.playlistmaker.player.ui

import android.media.MediaPlayer
import android.os.Handler
import android.os.Looper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.practicum.playlistmaker.search.domain.models.Track
import java.text.SimpleDateFormat
import java.util.Locale

class PlayerViewModel(private val track: Track): ViewModel() {
    companion object {
        fun getFactory(track: Track): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                PlayerViewModel(track)
            }
        }

        private const val DELAY_UPDATE_PROGRESS = 500L
    }

    private val playerLiveData = MutableLiveData<PlayerState>(PlayerState.Default())
    fun observePlayer(): LiveData<PlayerState> = playerLiveData

    private var mediaPlayer = MediaPlayer()

    private var handler = Handler(Looper.getMainLooper())

    private val timerRunnable = Runnable {
        if(playerLiveData.value?.isPlay == true) {
            startTimerUpdate()
        }
    }

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
        handler.removeCallbacks(timerRunnable)
        mediaPlayer.pause()
        playerLiveData.postValue(PlayerState.Paused(getProgressTime()))
    }

    /** Обновление таймера трека */
    private fun startTimerUpdate() {
        playerLiveData.postValue(PlayerState.Playing(getProgressTime()))
        handler.postDelayed(timerRunnable, DELAY_UPDATE_PROGRESS)
    }

    private fun resetTimer() {
        playerLiveData.postValue(PlayerState.Prepared())
        handler.removeCallbacks(timerRunnable)
    }

    private fun getProgressTime(): String {
        return SimpleDateFormat("mm:ss", Locale.getDefault()).format(mediaPlayer.currentPosition)
    }
}
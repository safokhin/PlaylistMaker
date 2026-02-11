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

        const val STATE_DEFAULT = 0 //  начальное состояние (плеер ещё не готов к работе)
        const val STATE_PREPARED = 1 // плеер готов воспроизводить
        const val STATE_PLAYING = 2 // воспроизведение идёт
        const val STATE_PAUSED = 3 // воспроизведение на паузе
    }


    private val playerStateLiveData = MutableLiveData(STATE_DEFAULT)
    fun observePlayerState(): LiveData<Int> = playerStateLiveData

    private val progressTimeLiveData = MutableLiveData("00:00")
    fun observeProgressTime(): LiveData<String> = progressTimeLiveData


    private val trackLiveData = MutableLiveData(track)
    fun observeTrack(): LiveData<Track> = trackLiveData

    private var mediaPlayer = MediaPlayer()

    private var handler = Handler(Looper.getMainLooper())

    private val timerRunnable = Runnable {
        if (playerStateLiveData.value == STATE_PLAYING) {
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
        when (playerStateLiveData.value) {
            STATE_PLAYING -> pausePlayer()
            STATE_PREPARED, STATE_PAUSED -> startPlayer()
        }
    }

    private fun preparePlayer() {
        mediaPlayer.setDataSource(track.previewUrl)
        mediaPlayer.prepareAsync()
        // Завершение подготовки
        mediaPlayer.setOnPreparedListener {
            playerStateLiveData.postValue(STATE_PREPARED)
        }
        // Завершение воспроизведения
        mediaPlayer.setOnCompletionListener {
            playerStateLiveData.postValue(STATE_PREPARED)
            resetTimer()
        }
    }

    private fun startPlayer() {
        mediaPlayer.start()
        playerStateLiveData.postValue(STATE_PLAYING)
        startTimerUpdate()
    }

    private fun pausePlayer() {
        pauseTimer()
        mediaPlayer.pause()
        playerStateLiveData.postValue(STATE_PAUSED)
    }

    /** Обновление таймера трека */
    private fun startTimerUpdate() {
        progressTimeLiveData.postValue(SimpleDateFormat("mm:ss", Locale.getDefault()).format(mediaPlayer.currentPosition))
        handler.postDelayed(timerRunnable, DELAY_UPDATE_PROGRESS)
    }

    /** Остановка таймера */
    private fun pauseTimer() {
        handler.removeCallbacks(timerRunnable)
    }

    private fun resetTimer() {
        handler.removeCallbacks(timerRunnable)
        progressTimeLiveData.postValue("00:00")
    }
}
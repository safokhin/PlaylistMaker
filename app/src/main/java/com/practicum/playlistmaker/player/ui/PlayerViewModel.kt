package com.practicum.playlistmaker.player.ui

import android.media.MediaPlayer
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.search.domain.db.FavoritesInteractor
import com.practicum.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale

class PlayerViewModel(
    private val track: Track,
    private val mediaPlayer: MediaPlayer,
    private val favoritesInteractor:FavoritesInteractor
): ViewModel() {
    companion object {
        private const val DELAY_UPDATE_PROGRESS = 300L
    }

    private val playerLiveData = MutableLiveData<PlayerState>(PlayerState.Default(track))
    fun observePlayer(): LiveData<PlayerState> = playerLiveData

    private var timerJob: Job? = null

    init {
        preparePlayer()
    }

    /** Отображение избранного */
    fun loadIsFavorite() {
        viewModelScope.launch {
            favoritesInteractor.favoriteTrackById(track.trackId.toString()).collect {
                if(it != null) {
                    updateTrackLiveData(playerLiveData.value!!.track.copy(isFavorite = true))
                }
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        mediaPlayer.release()
        resetTimer()
    }

    fun onPause() {
        pausePlayer()
    }

    /** Обработка клика на "Добавление в избранное" */
    fun favoriteHandler() {
        viewModelScope.launch {
            val updateTrack = playerLiveData.value!!.track.copy(isFavorite = !playerLiveData.value!!.track.isFavorite)
            updateTrackLiveData(updateTrack)

            if (updateTrack.isFavorite) {
                favoritesInteractor.addTrack(updateTrack)
            } else {
                favoritesInteractor.removeTrack(updateTrack)
            }
        }
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
            playerLiveData.postValue(PlayerState.Prepared(playerLiveData.value!!.track))
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
        playerLiveData.postValue(PlayerState.Paused(getProgressTime(), playerLiveData.value!!.track))
    }

    /** Обновление таймера трека */
    private fun startTimerUpdate() {
        playerLiveData.postValue(PlayerState.Playing(getProgressTime(), playerLiveData.value!!.track))
        timerJob = viewModelScope.launch {
            while (mediaPlayer.isPlaying) {
                delay(DELAY_UPDATE_PROGRESS)
                playerLiveData.postValue(PlayerState.Playing(getProgressTime(), playerLiveData.value!!.track))
            }
        }

    }

    private fun resetTimer() {
        playerLiveData.postValue(PlayerState.Prepared(playerLiveData.value!!.track))
        timerJob?.cancel()
    }

    private fun getProgressTime(): String {
        return SimpleDateFormat("mm:ss", Locale.getDefault()).format(mediaPlayer.currentPosition)
    }

    private fun updateTrackLiveData(track: Track) {
        playerLiveData.postValue(
            when(val state = playerLiveData.value) {
                is PlayerState.Default -> PlayerState.Default(track)
                is PlayerState.Prepared -> PlayerState.Prepared(track)
                is PlayerState.Playing -> PlayerState.Playing(state.progressTime, track)
                is PlayerState.Paused -> PlayerState.Paused(state.progressTime, track)
                else -> state
            }
        )
    }
}
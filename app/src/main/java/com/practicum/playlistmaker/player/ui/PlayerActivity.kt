package com.practicum.playlistmaker.player.ui

import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.google.gson.Gson
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.ActivityPlayerBinding
import com.practicum.playlistmaker.search.domain.models.Track
import com.practicum.playlistmaker.utils.Converter
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class PlayerActivity: AppCompatActivity() {
    private lateinit var binding: ActivityPlayerBinding

    private val gson: Gson by inject()
    private val viewModel: PlayerViewModel by viewModel {
        parametersOf(getTrack())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.player)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val track = getTrack()

        viewModel.observePlayer().observe(this) {
            changePlayerIcon(it.isPlay)
            enableButton(!it.disableButton)
            binding.timePlayer.text = it.progressTime
        }

        binding.playerControl.setOnClickListener {
            viewModel.playbackControl()
        }

        binding.btnBack.setNavigationOnClickListener {
            finish()
        }

        setTrackData(track)
    }

    override fun onPause() {
        super.onPause()
        viewModel.onPause()
    }

    private fun getTrack(): Track {
        return gson.fromJson(intent.getStringExtra(EXTRA_TRACK_KEY), Track::class.java)
    }

    private fun setTrackData(track: Track) {
        binding.apply {
            trackNamePlayer.text = track.trackName
            artistNamePlayer.text = track.artistName
            durationValuePlayer.text = track.trackTime
            albumValuePlayer.text = track.collectionName
            yearValuePlayer.text = track.releaseYear ?: ""
            genreValuePlayer.text = track.primaryGenreName
            countryValuePlayer.text = track.country
        }


        val roundedVal: Float = resources.getDimension(R.dimen.track_image_border_px)

        Glide.with(this)
            .load(track.artworkUrl100.replaceAfterLast('/',"512x512bb.jpg"))
            .placeholder(R.drawable.track_placeholder_icon)
            .transform(RoundedCorners(Converter.dpToPx(roundedVal, this)))
            .into(binding.trackImg)
    }

    /** Отрисовка кнопки */
    private fun changePlayerIcon(isPlay: Boolean) {
        if(isPlay) {
            binding.playerControl.setImageResource(R.drawable.button_stop)
        } else {
            binding.playerControl.setImageResource(R.drawable.button_play)
        }
    }

    /**
     * Активация кнопки
     * Пока не прогрузился плеер кнопка не доступна
     */
    private fun enableButton(isEnabled: Boolean) {
        binding.playerControl.isEnabled = isEnabled
    }

    companion object {
        const val EXTRA_TRACK_KEY = "extra_track"
    }
}
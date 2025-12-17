package com.practicum.playlistmaker

import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.google.android.material.appbar.MaterialToolbar
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.practicum.playlistmaker.utils.dpToPx
import java.text.SimpleDateFormat
import java.util.Locale

class PlayerActivity: AppCompatActivity() {
    private lateinit var trackImg: ImageView
    private lateinit var trackName: TextView
    private lateinit var trackArtist: TextView
    private lateinit var trackDuration: TextView
    private lateinit var trackAlbum: TextView
    private lateinit var trackYear: TextView
    private lateinit var trackGenre: TextView
    private lateinit var trackCountry: TextView
    private lateinit var timePlayer: TextView
    private lateinit var playerControl: ImageView

    private var track: Track? = null
    private var mediaPlayer = MediaPlayer()
    private var playerState = STATE_DEFAULT

    private var mainThreadHandler: Handler? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_player)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.player)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        playerControl = findViewById(R.id.playerControl)

        findViewById<MaterialToolbar>(R.id.btn_back).setNavigationOnClickListener {
            finish()
        }

        mainThreadHandler = Handler(Looper.getMainLooper())

        initTrack()
        init()
        preparePlayer()
    }

    override fun onPause() {
        super.onPause()
        pausePlayer()
    }

    override fun onDestroy() {
        super.onDestroy()
        stopProgress()
        mediaPlayer.release()
    }

    private fun initTrack() {
        val gson = Gson()
        val tokenType = object : TypeToken<Track>() {}.type

        track = try {
            gson.fromJson(intent.getStringExtra(EXTRA_TRACK_KEY), tokenType)
        } catch (_: Exception) {
            null
        }
    }

    /** Инициализация activity */
    private fun init() {
        val trackData = track ?: return

        trackImg = findViewById(R.id.trackImg)
        trackName = findViewById(R.id.trackNamePlayer)
        trackArtist = findViewById(R.id.artistNamePlayer)
        trackDuration = findViewById(R.id.durationValuePlayer)
        trackAlbum = findViewById(R.id.albumValuePlayer)
        trackYear = findViewById(R.id.yearValuePlayer)
        trackGenre = findViewById(R.id.genreValuePlayer)
        trackCountry = findViewById(R.id.countryValuePlayer)
        timePlayer = findViewById(R.id.timePlayer)

        trackName.text = trackData.trackName
        trackArtist.text = trackData.artistName
        trackDuration.text = SimpleDateFormat("mm:ss", Locale.getDefault()).format(trackData.trackTimeMillis)
        trackAlbum.text = trackData.collectionName
        trackYear.text = parseYear(trackData.releaseDate ?: "")
        trackGenre.text = trackData.primaryGenreName
        trackCountry.text = trackData.country

        val roundedVal: Float = resources.getDimension(R.dimen.track_image_border_px)

        Glide.with(this)
            .load(trackData.artworkUrl100.replaceAfterLast('/',"512x512bb.jpg"))
            .placeholder(R.drawable.track_placeholder_icon)
            .transform(RoundedCorners(dpToPx(roundedVal, this)))
            .into(trackImg)
    }

    private fun parseYear(date: String): String {
        val parser = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())

        return try {
            val date = parser.parse(date)

            if (date != null) {
                SimpleDateFormat("yyyy", Locale.getDefault()).format(date)
            } else {
                ""
            }
        } catch (_: Exception) {
            ""
        }
    }

    private fun preparePlayer() {
        val url = track?.previewUrl ?: return

        setTimerText(0)
        mediaPlayer.setDataSource(url)
        mediaPlayer.prepareAsync()
        // Завершение подготовки
        mediaPlayer.setOnPreparedListener {
            playerState = STATE_PREPARED
        }
        // Завершение воспроизведения
        mediaPlayer.setOnCompletionListener {
            playerControl.setImageResource(R.drawable.button_play)
            playerState = STATE_PREPARED
            stopProgress()
            setTimerText(0)
        }

        playerControl.setOnClickListener {
            playbackControl()
        }
    }

    private fun playbackControl() {
        when(playerState) {
            STATE_PLAYING -> {
                pausePlayer()
            }
            STATE_PREPARED, STATE_PAUSED -> {
                startPlayer()
            }
        }
    }

    private fun startPlayer() {
        mediaPlayer.start()
        playerControl.setImageResource(R.drawable.button_stop)
        playerState = STATE_PLAYING
        startProgress()
    }

    private fun pausePlayer() {
        mediaPlayer.pause()
        playerControl.setImageResource(R.drawable.button_play)
        playerState = STATE_PAUSED

        stopProgress()
    }

    private fun startProgress() {
        mainThreadHandler?.post(createUpdateProgress())
    }

    private fun stopProgress() {
        mainThreadHandler?.removeCallbacksAndMessages(null)
    }

    private fun createUpdateProgress(): Runnable {
        return object : Runnable {
            override fun run() {
                setTimerText(mediaPlayer.currentPosition)
                mainThreadHandler?.postDelayed(this, DELAY_UPDATE_PROGRESS)
            }
        }
    }

    private fun setTimerText(time: Int) {
        timePlayer.text = SimpleDateFormat("mm:ss", Locale.getDefault()).format(time)
    }

    companion object {
        const val EXTRA_TRACK_KEY = "extra_track"
        private const val STATE_DEFAULT = 0
        private const val STATE_PREPARED = 1
        private const val STATE_PLAYING = 2
        private const val STATE_PAUSED = 3

        private const val DELAY_UPDATE_PROGRESS = 500L
    }
}
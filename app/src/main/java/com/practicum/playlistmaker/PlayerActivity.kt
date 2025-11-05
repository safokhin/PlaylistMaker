package com.practicum.playlistmaker

import android.os.Bundle
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_player)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.player)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        findViewById<MaterialToolbar>(R.id.btn_back).setNavigationOnClickListener {
            finish()
        }


        bindTrack()
    }

    /** Заполение activity данными */
    private fun bindTrack() {
        val gson = Gson()
        val tokenType = object : TypeToken<Track>() {}.type

        trackImg = findViewById<ImageView>(R.id.trackImg)
        trackName = findViewById<TextView>(R.id.trackNamePlayer)
        trackArtist = findViewById<TextView>(R.id.artistNamePlayer)
        trackDuration = findViewById<TextView>(R.id.durationValuePlayer)
        trackAlbum = findViewById<TextView>(R.id.albumValuePlayer)
        trackYear = findViewById<TextView>(R.id.yearValuePlayer)
        trackGenre = findViewById<TextView>(R.id.genreValuePlayer)
        trackCountry = findViewById<TextView>(R.id.countryValuePlayer)
        timePlayer = findViewById<TextView>(R.id.timePlayer)

        val track: Track? = try {
            gson.fromJson(intent.getStringExtra(EXTRA_TRACK_KEY), tokenType)
        } catch (_: Exception) {
            null
        }

        if(track != null) {
            trackName.text = track.trackName
            trackArtist.text = track.artistName
            trackDuration.text = SimpleDateFormat("mm:ss", Locale.getDefault()).format(track.trackTimeMillis)
            timePlayer.text = SimpleDateFormat("mm:ss", Locale.getDefault()).format(track.trackTimeMillis) // Пока просто время трека, в дальнейшем изменить
            trackAlbum.text = track.collectionName
            trackYear.text = parseYear(track.releaseDate ?: "")
            trackGenre.text = track.primaryGenreName
            trackCountry.text = track.country

            val roundedVal: Float = resources.getDimension(R.dimen.track_image_border_px)

            Glide.with(this)
                .load(track.artworkUrl100.replaceAfterLast('/',"512x512bb.jpg"))
                .placeholder(R.drawable.track_placeholder_icon)
                .transform(RoundedCorners(dpToPx(roundedVal, this)))
                .into(trackImg)
        }
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

    companion object {
        const val EXTRA_TRACK_KEY = "extra_track"
    }
}
package com.practicum.playlistmaker

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.practicum.playlistmaker.utils.dpToPx
import java.text.SimpleDateFormat
import java.util.Locale

class TrackViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
    private val sourceTrackName = itemView.findViewById<TextView>(R.id.trackName)
    private val sourceArtistName = itemView.findViewById<TextView>(R.id.artistName)
    private val sourceTrackTime = itemView.findViewById<TextView>(R.id.trackTime)
    private val sourceArtworkUrl100 = itemView.findViewById<ImageView>(R.id.artworkUrl100)

    fun bind(model: Track) {
        sourceTrackName.text = model.trackName
        sourceArtistName.text = model.artistName
        sourceTrackTime.text = SimpleDateFormat("mm:ss", Locale.getDefault()).format(model.trackTimeMillis)

        val roundedVal: Float = itemView.context.resources.getDimension(R.dimen.track_image_border_px)

        Glide.with(itemView)
            .load(model.artworkUrl100)
            .placeholder(R.drawable.track_placeholder_icon)
            .transform(RoundedCorners(dpToPx(roundedVal, itemView.context)))
            .into(sourceArtworkUrl100)
    }
}
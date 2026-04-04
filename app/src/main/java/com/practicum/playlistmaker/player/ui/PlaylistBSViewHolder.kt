package com.practicum.playlistmaker.player.ui

import android.net.Uri
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.search.domain.models.Playlist
import com.practicum.playlistmaker.utils.Converter
import java.io.File

class PlaylistBSViewHolder(itemView: View): RecyclerView.ViewHolder(itemView)  {
    private val name = itemView.findViewById<TextView>(R.id.playlistItemTrackName)
    private val countTracks = itemView.findViewById<TextView>(R.id.playlistItemTrackCount)
    private val img = itemView.findViewById<ImageView>(R.id.playlistItemTrackImg)

    fun bind(model: Playlist) {
        name.text = model.name
        countTracks.text = itemView.context.resources.getQuantityString(
            R.plurals.playlist_count,
            model.tracksId.size,
            model.tracksId.size
        )

        val roundedVal: Float = itemView.context.resources.getDimension(R.dimen.track_image_border_px)

        Glide.with(itemView)
            .load(Uri.fromFile(File(model.uri ?: "")))
            .placeholder(R.drawable.track_placeholder_icon)
            .transform(RoundedCorners(Converter.dpToPx(roundedVal, itemView.context)))
            .into(img)
    }
}
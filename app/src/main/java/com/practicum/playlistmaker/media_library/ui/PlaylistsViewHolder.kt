package com.practicum.playlistmaker.media_library.ui

import android.net.Uri
import android.util.Log
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
import com.bumptech.glide.load.resource.bitmap.CenterCrop

class PlaylistsViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
    private val name = itemView.findViewById<TextView>(R.id.playlistItemLargeName)
    private val countTracks = itemView.findViewById<TextView>(R.id.playlistItemLargeCount)
    private val img = itemView.findViewById<ImageView>(R.id.playlistItemLargeImg)

    fun bind(model: Playlist) {
        name.text = model.name
        countTracks.text = itemView.context.resources.getQuantityString(
            R.plurals.playlist_count,
            model.tracksCount,
            model.tracksCount
        )

        val roundedVal: Float = itemView.context.resources.getDimension(R.dimen.playlist_image_border_px)

        Glide.with(itemView)
            .load(Uri.fromFile(File(model.uri ?: "")))
            .placeholder(R.drawable.track_placeholder_icon)
            .transform(CenterCrop(), RoundedCorners(Converter.dpToPx(roundedVal, itemView.context)))
            .into(img)
    }
}
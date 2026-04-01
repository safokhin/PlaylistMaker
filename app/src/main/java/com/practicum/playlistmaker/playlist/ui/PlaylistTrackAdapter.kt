package com.practicum.playlistmaker.playlist.ui

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.search.domain.models.Track

class PlaylistTrackAdapter(
    private val onClick: (Track) -> Unit,
    private val onLongClick: (Track) -> Unit,
): RecyclerView.Adapter<PlaylistTrackViewHolder>() {
    var list = ArrayList<Track>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaylistTrackViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.track_view, parent, false)
        return PlaylistTrackViewHolder(view)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: PlaylistTrackViewHolder, position: Int) {
        holder.itemView.setOnClickListener { onClick(list[position]) }
        holder.itemView.setOnLongClickListener {
            onLongClick(list[position])
            true
        }
        holder.bind(list[position])
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setList(newList: List<Track>) {
        list.clear()
        list.addAll(newList)
        notifyDataSetChanged()
    }
}
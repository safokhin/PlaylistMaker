package com.practicum.playlistmaker.player.ui

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.search.domain.models.Playlist

class PlaylistBSAdapter(private val onClick: (Playlist) -> Unit): RecyclerView.Adapter<PlaylistBSViewHolder>() {
    var list = ArrayList<Playlist>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaylistBSViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.playlist_item, parent, false)
        return PlaylistBSViewHolder(view)
    }

    override fun onBindViewHolder(holder: PlaylistBSViewHolder, position: Int) {
        holder.itemView.setOnClickListener { onClick(list[position]) }
        holder.bind(list[position])
    }

    override fun getItemCount(): Int {
        return list.size
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setList(newList: List<Playlist>) {
        list.clear()
        list.addAll(newList)
        notifyDataSetChanged()
    }
}
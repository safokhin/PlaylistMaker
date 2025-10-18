package com.practicum.playlistmaker

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class TrackAdapter(
    val list: MutableList<Track>,
    private val onClick: (Track) -> Unit
): RecyclerView.Adapter<TrackViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.track_view, parent, false)
        return TrackViewHolder(view)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: TrackViewHolder, position: Int) {
        holder.itemView.setOnClickListener { onClick(list[position]) }
        holder.bind(list[position])
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setList(newList: List<Track>) {
        list.clear()
        list.addAll(newList)
        notifyDataSetChanged()
    }
}
package com.practicum.playlistmaker.search.ui

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.search.domain.models.Track

class TrackAdapter(private val onClick: (Track) -> Unit): RecyclerView.Adapter<TrackViewHolder>() {
    var list = ArrayList<Track>()

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
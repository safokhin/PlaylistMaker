package com.practicum.playlistmaker.search.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorite_table")
data class TrackEntity (
    @PrimaryKey
    val trackId: Long,
    val trackName: String,
    val artistName: String,
    val trackTime: String,
    val artworkUrl100: String,
    val collectionName: String?,
    val releaseYear: String?,
    val primaryGenreName: String,
    val country: String,
    val previewUrl: String,
    val addedAt: Long = System.currentTimeMillis()
)
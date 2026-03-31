package com.practicum.playlistmaker.search.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.practicum.playlistmaker.search.data.converters.Converters
import com.practicum.playlistmaker.search.data.db.dao.PlaylistDao
import com.practicum.playlistmaker.search.data.db.dao.TrackDao
import com.practicum.playlistmaker.search.data.db.dao.TrackPlaylistDao
import com.practicum.playlistmaker.search.data.db.entity.PlaylistEntity
import com.practicum.playlistmaker.search.data.db.entity.TrackEntity
import com.practicum.playlistmaker.search.data.db.entity.TrackPlaylistEntity

@Database(version = 1, exportSchema = false, entities = [TrackEntity::class, PlaylistEntity::class, TrackPlaylistEntity::class])
@TypeConverters(Converters::class)
abstract class AppDatabase: RoomDatabase() {

    abstract fun trackDao(): TrackDao

    abstract fun playlistDao(): PlaylistDao

    abstract fun trackPlaylistDao(): TrackPlaylistDao
}
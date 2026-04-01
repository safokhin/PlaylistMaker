package com.practicum.playlistmaker.search.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.practicum.playlistmaker.search.data.db.entity.TrackPlaylistEntity

@Dao
interface TrackPlaylistDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertTrack(track: TrackPlaylistEntity)

    @Query("DELETE FROM track_playlist_table WHERE trackId = :id")
    suspend fun removeTrack(id: String)

    @Query("SELECT * FROM track_playlist_table")
    suspend fun getTracks(): List<TrackPlaylistEntity>
}
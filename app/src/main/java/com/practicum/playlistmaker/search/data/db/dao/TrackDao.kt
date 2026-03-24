package com.practicum.playlistmaker.search.data.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.practicum.playlistmaker.search.data.db.entity.TrackEntity

@Dao
interface TrackDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTrack(tracks: List<TrackEntity>)

    @Delete(entity = TrackEntity::class)
    suspend fun removeTrack(tracks: List<TrackEntity>)

    @Query("SELECT * FROM favorite_table ORDER BY addedAt DESC")
    suspend fun getTracks(): List<TrackEntity>

    @Query("SELECT * FROM favorite_table WHERE trackId = :id")
    suspend fun getTrackById(id: String): TrackEntity?
}
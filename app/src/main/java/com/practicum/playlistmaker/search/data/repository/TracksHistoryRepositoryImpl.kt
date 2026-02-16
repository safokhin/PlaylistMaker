package com.practicum.playlistmaker.search.data.repository

import android.content.Context
import androidx.core.content.edit
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.practicum.playlistmaker.search.domain.api.TracksHistoryRepository
import com.practicum.playlistmaker.search.domain.models.Track

class TracksHistoryRepositoryImpl(
    private val gson: Gson,
    context: Context
): TracksHistoryRepository {
    private val tokenType = object : TypeToken<List<Track>>() {}.type
    private val sharedPreferences = context.getSharedPreferences(SHARED_PREFS_FILE, Context.MODE_PRIVATE)

    override fun addHistory(track: Track) {
        val history = ArrayList(getHistory().filter { it.trackId != track.trackId })
        history.add(0, track)

        if(history.size > 10) {
            history.removeAt(9)
        }

        sharedPreferences.edit { putString(SHARED_PREFS_KEY, gson.toJson(history)) }
    }

    override fun getHistory(): List<Track> {
        val dataStr = sharedPreferences.getString(SHARED_PREFS_KEY, null) ?: return ArrayList<Track>()

        return try {
            gson.fromJson(dataStr, tokenType) ?: ArrayList()
        } catch (_: Exception) {
            ArrayList()
        }
    }

    override fun clearHistory() {
        sharedPreferences.edit { remove(SHARED_PREFS_KEY) }
    }

    companion object {
        const val SHARED_PREFS_FILE = "app_shared_prefs"
        const val SHARED_PREFS_KEY = "history_tracks"
    }
}
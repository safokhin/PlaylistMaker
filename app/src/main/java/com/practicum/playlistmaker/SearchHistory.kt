package com.practicum.playlistmaker

import android.content.Context
import android.content.SharedPreferences.OnSharedPreferenceChangeListener
import android.util.Log
import com.google.gson.Gson
import androidx.core.content.edit
import com.google.gson.reflect.TypeToken

class SearchHistory(context: Context) {
    private val tokenType = object : TypeToken<List<Track>>() {}.type
    private val sharedPreferences = context.getSharedPreferences(SHARED_PREFS_FILE, Context.MODE_PRIVATE)
    private val gson = Gson()

    fun getTracks(): ArrayList<Track> {
        val dataStr = sharedPreferences.getString(SHARED_PREFS_KEY, null) ?: return ArrayList<Track>()

        return try {
            gson.fromJson(dataStr, tokenType) ?: ArrayList<Track>()
        } catch (_: Exception) {
            ArrayList<Track>()
        }
    }

    fun addTracks(track: Track) {
        val trackList = ArrayList(getTracks().filter { it.trackId != track.trackId })
        trackList.add(0, track)

        if(trackList.size > 10) {
            trackList.removeAt(9)
        }

        Log.i("CLICK", track.toString())
        sharedPreferences.edit { putString(SHARED_PREFS_KEY, gson.toJson(trackList)) }
    }

    fun clearTracks() {
        sharedPreferences.edit { remove(SHARED_PREFS_KEY) }
    }

    fun registerListener(listener: OnSharedPreferenceChangeListener) {
        sharedPreferences.registerOnSharedPreferenceChangeListener(listener)
    }

    fun unregisterListener(listener: OnSharedPreferenceChangeListener) {
        sharedPreferences.unregisterOnSharedPreferenceChangeListener(listener)
    }

    companion object {
        const val SHARED_PREFS_FILE = ADD_SHARED_PREFS
        const val SHARED_PREFS_KEY = "history_tracks"
    }
}
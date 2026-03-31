package com.practicum.playlistmaker.search.data.converters

import androidx.room.ProvidedTypeConverter
import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

@ProvidedTypeConverter
class Converters(private val gson: Gson) {
    @TypeConverter
    fun fromList(list: List<Long>?): String {
        return gson.toJson(list)
    }

    @TypeConverter
    fun toList(data: String?): List<Long> {
        if (data.isNullOrEmpty()) return emptyList()

        val type = object : TypeToken<List<Long>>() {}.type

        return gson.fromJson(data, type)
    }
}
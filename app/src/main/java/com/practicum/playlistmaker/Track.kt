package com.practicum.playlistmaker

/**
 * Трек
 * @param trackId - id композиции
 * @param trackName - название композиции
 * @param artistName - имя исполнителя
 * @param trackTime - продолжительность трека
 * @param artworkUrl100 - ссылка на изображение обложки
 */
data class Track(
    val trackId: Long,
    val trackName: String,
    val artistName: String,
    val trackTimeMillis: Long,
    val artworkUrl100: String
)

class TrackResponse(val resultCount: Int, val results: List<Track>)
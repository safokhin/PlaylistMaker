package com.practicum.playlistmaker

/**
 * Трек
 * @param trackName - название композиции
 * @param artistName - имя исполнителя
 * @param trackTime - продолжительность трека
 * @param artworkUrl100 - ссылка на изображение обложки
 */
data class Track(
    val trackName: String,
    val artistName: String,
    val trackTime: String,
    val artworkUrl100: String
)
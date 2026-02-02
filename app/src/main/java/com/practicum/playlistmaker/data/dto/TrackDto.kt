package com.practicum.playlistmaker.data.dto

/**
 * Трек
 * @param trackId - id композиции
 * @param trackName - название композиции
 * @param artistName - имя исполнителя
 * @param trackTimeMillis - продолжительность трека
 * @param artworkUrl100 - ссылка на изображение обложки
 * @param collectionName - название альбома
 * @param releaseDate - дата выхода
 * @param primaryGenreName - название жанра
 * @param country - название страны
 * @param previewUrl - ссылка
 */
data class TrackDto(
    val trackId: Long,
    val trackName: String,
    val artistName: String,
    val trackTimeMillis: Long,
    val artworkUrl100: String,
    val collectionName: String?,
    val releaseDate: String?,
    val primaryGenreName: String,
    val country: String,
    val previewUrl: String
)
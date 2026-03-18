package com.practicum.playlistmaker.search.data.repository

import com.practicum.playlistmaker.search.data.network.ItunesApiService
import com.practicum.playlistmaker.search.domain.api.TracksSearchRepository
import com.practicum.playlistmaker.search.domain.models.Track
import com.practicum.playlistmaker.utils.Converter
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.text.SimpleDateFormat
import java.util.Locale
import kotlin.coroutines.cancellation.CancellationException

class TracksSearchRepositoryImpl(private val api: ItunesApiService): TracksSearchRepository {

    /** Получение данных с сервера и преобразование в domain.models.Track */
    override fun searchTracks(expression: String): Flow<Result<List<Track>>> = flow {
        try {
            val response = api.findTracks(expression)

            if(response.code() == 200) {
                val convertTracks = response.body()?.results?.map {
                    Track(
                        trackId = it.trackId,
                        trackName = it.trackName,
                        artistName = it.artistName,
                        artworkUrl100 = it.artworkUrl100,
                        collectionName = it.collectionName,
                        releaseYear = Converter.dateToYear(it.releaseDate),
                        primaryGenreName = it.primaryGenreName,
                        country = it.country,
                        previewUrl = it.previewUrl,
                        trackTime = SimpleDateFormat("mm:ss", Locale.getDefault()).format(it.trackTimeMillis)
                    )
                }.orEmpty()

                emit(Result.success(convertTracks))
            } else {
                emit(Result.failure(Exception("Ошибка ${response.code()}")))
            }

        } catch (e: Exception) {
            if (e is CancellationException) throw e
            emit(Result.failure(e))
        }
    }
}
package com.practicum.playlistmaker.data.repository

import com.practicum.playlistmaker.data.dto.TrackSearchResponse
import com.practicum.playlistmaker.data.network.ItunesApiService
import com.practicum.playlistmaker.domain.api.TracksSearchRepository
import com.practicum.playlistmaker.domain.models.Track
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.Locale

class TracksSearchRepositoryImpl(private val api: ItunesApiService): TracksSearchRepository {

    /** Получение данных с сервера и преобразование в domain.models.Track */
    override fun searchTracks(expression: String, callback: (Result<List<Track>>) -> Unit) {
        api.findTracks(expression).enqueue(object : Callback<TrackSearchResponse> {
            override fun onResponse(
                call: Call<TrackSearchResponse?>,
                response: Response<TrackSearchResponse?>
            ) {
                if(response.code() == 200) {
                    val convertTracks = response.body()?.results?.map {
                        Track(
                            trackId = it.trackId,
                            trackName = it.trackName,
                            artistName = it.artistName,
                            artworkUrl100 = it.artworkUrl100,
                            collectionName = it.collectionName,
                            releaseDate = it.releaseDate,
                            primaryGenreName = it.primaryGenreName,
                            country = it.country,
                            previewUrl = it.previewUrl,
                            trackTime = SimpleDateFormat("mm:ss", Locale.getDefault()).format(it.trackTimeMillis)
                        )
                    }.orEmpty()

                    callback(Result.success(convertTracks))
                } else {
                    callback(Result.failure(Exception("Ошибка ${response.code()}")))
                }
            }

            override fun onFailure(
                call: Call<TrackSearchResponse?>,
                t: Throwable
            ) {
                callback(Result.failure(t))
            }
        })
    }
}
package com.practicum.playlistmaker.search.data.network

import com.practicum.playlistmaker.search.data.dto.TrackSearchResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface ItunesApiService {
    @GET("search?entity=song")
    suspend fun findTracks(@Query("term") text: String): Response<TrackSearchResponse>
}
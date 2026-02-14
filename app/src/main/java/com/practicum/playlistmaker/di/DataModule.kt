package com.practicum.playlistmaker.di

import android.content.Context
import android.media.MediaPlayer
import com.google.gson.Gson
import com.practicum.playlistmaker.search.data.network.ItunesApiService
import com.practicum.playlistmaker.settings.data.ExternalNavigator
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
val dataModule = module {
    single<ItunesApiService> {
        Retrofit.Builder()
            .baseUrl("https://itunes.apple.com")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ItunesApiService::class.java)
    }

    single {
        androidContext()
            .getSharedPreferences("APP_PREFERENCES", Context.MODE_PRIVATE)
    }

    factory { Gson() }

    factory { MediaPlayer() }

    single {
        ExternalNavigator(androidContext())
    }
}
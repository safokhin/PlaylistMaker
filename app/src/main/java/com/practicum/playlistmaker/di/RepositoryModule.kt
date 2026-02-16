package com.practicum.playlistmaker.di

import com.practicum.playlistmaker.search.data.repository.TracksHistoryRepositoryImpl
import com.practicum.playlistmaker.search.data.repository.TracksSearchRepositoryImpl
import com.practicum.playlistmaker.search.domain.api.TracksHistoryRepository
import com.practicum.playlistmaker.search.domain.api.TracksSearchRepository
import com.practicum.playlistmaker.settings.data.SettingsRepositoryImpl
import com.practicum.playlistmaker.settings.domain.api.SettingsRepository
import org.koin.dsl.module

val repositoryModule = module {
    factory<SettingsRepository> {
        SettingsRepositoryImpl(get())
    }

    factory<TracksHistoryRepository> {
        TracksHistoryRepositoryImpl(get(), get())
    }

    factory<TracksSearchRepository> {
        TracksSearchRepositoryImpl(get())
    }
}
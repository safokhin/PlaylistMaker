package com.practicum.playlistmaker.di

import com.practicum.playlistmaker.search.data.converters.PlaylistDbConvertor
import com.practicum.playlistmaker.search.data.converters.TrackDbConvertor
import com.practicum.playlistmaker.search.data.converters.TrackPlaylistDbConvertor
import com.practicum.playlistmaker.search.data.repository.FavoritesRepositoryImpl
import com.practicum.playlistmaker.search.data.repository.PlaylistRepositoryImpl
import com.practicum.playlistmaker.search.data.repository.TracksHistoryRepositoryImpl
import com.practicum.playlistmaker.search.data.repository.TracksSearchRepositoryImpl
import com.practicum.playlistmaker.search.domain.api.TracksHistoryRepository
import com.practicum.playlistmaker.search.domain.api.TracksSearchRepository
import com.practicum.playlistmaker.search.domain.db.FavoritesRepository
import com.practicum.playlistmaker.search.domain.db.PlaylistRepository
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

    factory<FavoritesRepository> {
        FavoritesRepositoryImpl(get(), get())
    }

    factory { TrackDbConvertor() }

    factory { PlaylistDbConvertor() }

    factory { TrackPlaylistDbConvertor() }

    factory<PlaylistRepository> {
        PlaylistRepositoryImpl(get(), get(), get())
    }
}
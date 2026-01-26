package com.practicum.playlistmaker

import android.content.Context
import com.practicum.playlistmaker.data.network.RetrofitNetworkClient
import com.practicum.playlistmaker.data.repository.SettingsRepositoryImpl
import com.practicum.playlistmaker.data.repository.TracksHistoryRepositoryImpl
import com.practicum.playlistmaker.data.repository.TracksSearchRepositoryImpl
import com.practicum.playlistmaker.domain.api.SettingsInteractor
import com.practicum.playlistmaker.domain.api.SettingsRepository
import com.practicum.playlistmaker.domain.api.TracksHistoryInteractor
import com.practicum.playlistmaker.domain.api.TracksHistoryRepository
import com.practicum.playlistmaker.domain.api.TracksSearchInteractor
import com.practicum.playlistmaker.domain.api.TracksSearchRepository
import com.practicum.playlistmaker.domain.impl.SettingsInteractorImpl
import com.practicum.playlistmaker.domain.impl.TracksHistoryInteractorImpl
import com.practicum.playlistmaker.domain.impl.TracksSearchInteractorImpl

object Creator {
    private fun provideTracksSearchRepository(): TracksSearchRepository {
        return TracksSearchRepositoryImpl(RetrofitNetworkClient.instance)
    }

    private fun provideTracksHistoryRepository(context: Context): TracksHistoryRepository {
        return TracksHistoryRepositoryImpl(context)
    }

    private fun provideSettingsRepository(context: Context): SettingsRepository {
        return SettingsRepositoryImpl(context)
    }

    fun provideTracksSearchInteractor(): TracksSearchInteractor {
        return TracksSearchInteractorImpl(provideTracksSearchRepository())
    }

    fun provideTracksHistoryInteractor(context: Context): TracksHistoryInteractor {
        return TracksHistoryInteractorImpl(provideTracksHistoryRepository(context))
    }

    fun provideSettingsInteractor(context: Context) : SettingsInteractor {
        return SettingsInteractorImpl(provideSettingsRepository(context))
    }
}
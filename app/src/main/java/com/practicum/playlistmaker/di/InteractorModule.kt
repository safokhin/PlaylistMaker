package com.practicum.playlistmaker.di

import com.practicum.playlistmaker.search.domain.api.TracksHistoryInteractor
import com.practicum.playlistmaker.search.domain.api.TracksSearchInteractor
import com.practicum.playlistmaker.search.domain.impl.TracksHistoryInteractorImpl
import com.practicum.playlistmaker.search.domain.impl.TracksSearchInteractorImpl
import com.practicum.playlistmaker.settings.domain.api.SettingsInteractor
import com.practicum.playlistmaker.settings.domain.api.SharingInteractor
import com.practicum.playlistmaker.settings.domain.impl.SettingsInteractorImpl
import com.practicum.playlistmaker.settings.domain.impl.SharingInteractorImpl
import org.koin.dsl.module

val interactorModule = module {
    factory<SettingsInteractor> {
        SettingsInteractorImpl(get())
    }

    factory<SharingInteractor> {
        SharingInteractorImpl(get())
    }

    factory<TracksHistoryInteractor> {
        TracksHistoryInteractorImpl(get())
    }

    factory<TracksSearchInteractor> {
        TracksSearchInteractorImpl(get())
    }
}
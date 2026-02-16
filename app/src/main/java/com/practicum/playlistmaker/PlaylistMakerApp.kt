package com.practicum.playlistmaker

import android.app.Application
import org.koin.core.context.startKoin
import com.practicum.playlistmaker.di.dataModule
import com.practicum.playlistmaker.di.interactorModule
import com.practicum.playlistmaker.di.repositoryModule
import com.practicum.playlistmaker.di.viewModelModule
import com.practicum.playlistmaker.settings.domain.api.SettingsInteractor
import org.koin.android.ext.android.inject
import org.koin.android.ext.koin.androidContext

class PlaylistMakerApp: Application() {

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@PlaylistMakerApp)
            modules(dataModule, repositoryModule, interactorModule, viewModelModule)
        }

        initTheme()
    }

    private fun initTheme() {
        val settingsInteractor: SettingsInteractor by inject()
        settingsInteractor.setDarkTheme(settingsInteractor.isDarkTheme())
    }
}
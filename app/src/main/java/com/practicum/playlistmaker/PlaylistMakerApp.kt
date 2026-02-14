package com.practicum.playlistmaker

import android.app.Application
import com.practicum.playlistmaker.creator.Creator

class PlaylistMakerApp: Application() {

    override fun onCreate() {
        super.onCreate()

        // Установка темы
        val settingsInteractor = Creator.provideSettingsInteractor(this)
        settingsInteractor.setDarkTheme(settingsInteractor.isDarkTheme())
    }
}
package com.practicum.playlistmaker

import android.app.Application

class App: Application() {
    lateinit var themeStorage: ThemeStorage

    override fun onCreate() {
        super.onCreate()

        themeStorage = ThemeStorage(this)
        themeStorage.setTheme(themeStorage.isDarkTheme())
    }

}
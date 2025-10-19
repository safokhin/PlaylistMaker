package com.practicum.playlistmaker

import android.content.Context
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.edit

class ThemeStorage(
    private val context: Context
) {
    private val sharedPrefsSettings = context.getSharedPreferences(SHARED_PREFS_FILE, Context.MODE_PRIVATE)

    fun isDarkTheme(): Boolean {
        return sharedPrefsSettings.getBoolean(SHARED_PREFS_KEY, isDarkThemeDevice())
    }

    fun setTheme(darkThemeEnabled: Boolean) {
        sharedPrefsSettings.edit { putBoolean(SHARED_PREFS_KEY, darkThemeEnabled) }

        AppCompatDelegate.setDefaultNightMode(
            if (darkThemeEnabled) AppCompatDelegate.MODE_NIGHT_YES
            else AppCompatDelegate.MODE_NIGHT_NO
        )
    }


    /** Темная ли тема на устройстве */
    fun isDarkThemeDevice(): Boolean {
        return when (context.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) {
            Configuration.UI_MODE_NIGHT_YES -> true
            Configuration.UI_MODE_NIGHT_NO -> false
            else -> false
        }
    }

    companion object {
        const val SHARED_PREFS_FILE = APP_SETTINGS_SHARED_PREFS
        const val SHARED_PREFS_KEY = "app_theme"
    }
}
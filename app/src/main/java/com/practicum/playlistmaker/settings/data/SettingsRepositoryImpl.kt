package com.practicum.playlistmaker.settings.data

import android.content.Context
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.edit
import com.practicum.playlistmaker.settings.domain.api.SettingsRepository

class SettingsRepositoryImpl(context: Context): SettingsRepository {
    private val sharedPrefsSettings = context.getSharedPreferences(SHARED_PREFS_FILE, Context.MODE_PRIVATE)
    private val uiMode = context.resources.configuration.uiMode

    /** Темная ли тема на устройстве */
    private fun isDarkThemeDevice(): Boolean {
        return when (uiMode and Configuration.UI_MODE_NIGHT_MASK) {
            Configuration.UI_MODE_NIGHT_YES -> true
            Configuration.UI_MODE_NIGHT_NO -> false
            else -> false
        }
    }

    override fun isDarkTheme(): Boolean {
        return sharedPrefsSettings.getBoolean(SHARED_PREFS_KEY, isDarkThemeDevice())
    }

    override fun setDarkTheme(enabled: Boolean) {
        sharedPrefsSettings.edit { putBoolean(SHARED_PREFS_KEY, enabled) }

        AppCompatDelegate.setDefaultNightMode(
            if (enabled) AppCompatDelegate.MODE_NIGHT_YES
            else AppCompatDelegate.MODE_NIGHT_NO
        )
    }

    companion object {
        const val SHARED_PREFS_FILE = "app_settings"
        const val SHARED_PREFS_KEY = "app_theme"
    }
}
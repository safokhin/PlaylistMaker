package com.practicum.playlistmaker.settings.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.practicum.playlistmaker.settings.domain.api.SettingsInteractor
import com.practicum.playlistmaker.settings.domain.api.SharingInteractor

/**
 *
 * Описание:
 * В SettingsViewModel передавать только контекст всего приложения. Не передавать контекст Activity
 */
class SettingsViewModel(
    private val settingsInteractor: SettingsInteractor,
    private val sharingInteractor: SharingInteractor,
): ViewModel() {
    private val isDarkLiveData = MutableLiveData(settingsInteractor.isDarkTheme())
    fun observeIsDark(): LiveData<Boolean> = isDarkLiveData

    fun shareApp() {
        sharingInteractor.shareApp()
    }

    fun openSupport() {
        sharingInteractor.openSupport()
    }

    fun openTerms() {
        sharingInteractor.openTerms()
    }

    fun setDarkTheme(isDark: Boolean) {
        isDarkLiveData.postValue(isDark)
        settingsInteractor.setDarkTheme(isDark)
    }
}
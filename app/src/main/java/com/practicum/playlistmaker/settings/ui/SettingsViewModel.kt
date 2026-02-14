package com.practicum.playlistmaker.settings.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import com.practicum.playlistmaker.PlaylistMakerApp
import com.practicum.playlistmaker.creator.Creator
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
    companion object {
        fun getFactory(): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val app = (this[APPLICATION_KEY] as PlaylistMakerApp)
                SettingsViewModel(
                    Creator.provideSettingsInteractor(app),
                    Creator.provideSharingInteractor(app)
                )
            }
        }
    }

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
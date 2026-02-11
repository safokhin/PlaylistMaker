package com.practicum.playlistmaker.settings.domain.impl

import com.practicum.playlistmaker.settings.data.ExternalNavigator
import com.practicum.playlistmaker.settings.domain.api.SharingInteractor

class SharingInteractorImpl(private val externalNavigator: ExternalNavigator): SharingInteractor {
    override fun shareApp() {
        externalNavigator.shareApp()
    }

    override fun openTerms() {
        externalNavigator.openTerms()
    }

    override fun openSupport() {
        externalNavigator.openSupport()
    }
}
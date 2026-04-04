package com.practicum.playlistmaker.di

import com.practicum.playlistmaker.media_library.ui.FavoritesViewModel
import com.practicum.playlistmaker.media_library.ui.PlaylistsViewModel
import com.practicum.playlistmaker.new_playlist.ui.EditPlaylistViewModel
import com.practicum.playlistmaker.new_playlist.ui.NewPlaylistViewModel
import com.practicum.playlistmaker.player.ui.PlayerViewModel
import com.practicum.playlistmaker.player.ui.PlaylistBSViewModel
import com.practicum.playlistmaker.playlist.ui.PlaylistViewModel
import com.practicum.playlistmaker.search.ui.SearchViewModel
import com.practicum.playlistmaker.settings.ui.SettingsViewModel
import com.practicum.playlistmaker.search.domain.models.Track
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {
    viewModel {
        SettingsViewModel(get(), get())
    }

    viewModel {
        SearchViewModel(get(), get())
    }

    viewModel { (track: Track) ->
        PlayerViewModel(track, get(), get())
    }

    viewModel {
        FavoritesViewModel(get(), get())
    }

    viewModel {
        PlaylistsViewModel(get())
    }

    viewModel {
        NewPlaylistViewModel(get())
    }

    viewModel {
        PlaylistBSViewModel(get())
    }

    viewModel {
        PlaylistViewModel(get())
    }

    viewModel {
        EditPlaylistViewModel(get())
    }
}
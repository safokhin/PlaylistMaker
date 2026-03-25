package com.practicum.playlistmaker.media_library.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.FragmentFavoritesBinding
import com.practicum.playlistmaker.player.ui.PlayerFragment
import com.practicum.playlistmaker.search.domain.models.Track
import com.practicum.playlistmaker.search.ui.TrackAdapter
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel


class FavoritesFragment : Fragment() {
    companion object {
        fun newInstance() = FavoritesFragment()
        private const val CLICK_DEBOUNCE_DELAY = 1000L
    }

    private lateinit var binding: FragmentFavoritesBinding
    private val viewModel by viewModel<FavoritesViewModel>()

    private var trackAdapter = TrackAdapter { selectTrackHandler(it) }

    private var isClickAllowed = true

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFavoritesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.favoriteTracksList.adapter = trackAdapter

        viewModel.observeFavorites().observe(viewLifecycleOwner) {
            if (it.isEmpty()) {
                showPlaceholderEmpty()
            } else {
                showFavoriteTracksList(it)
            }
        }

        viewModel.loadFavoritesTracks()
    }

    private fun showPlaceholderEmpty() {
        binding.apply {
            placeholderEmpty.visibility = View.VISIBLE
            favoriteTracksList.visibility = View.GONE
        }
    }

    private fun showFavoriteTracksList(tracks: List<Track>) {
        binding.apply {
            placeholderEmpty.visibility = View.GONE
            favoriteTracksList.visibility = View.VISIBLE
        }

        trackAdapter.setList(tracks)
    }

    /** Обработчик клика при выборе трека */
    private fun selectTrackHandler(track: Track) {
        if (clickDebounce()) {
            viewModel.addHistory(track)

            findNavController().navigate(R.id.action_mediaLibraryFragment_to_playerFragment,
                bundleOf(PlayerFragment.EXTRA_TRACK_KEY to track))
        }
    }

    private fun clickDebounce() : Boolean {
        val current = isClickAllowed

        if (isClickAllowed) {
            isClickAllowed = false

            lifecycleScope.launch {
                delay(CLICK_DEBOUNCE_DELAY)
                isClickAllowed = true
            }
        }

        return current
    }
}


package com.practicum.playlistmaker.media_library.ui

import android.os.Bundle
import androidx.core.os.bundleOf
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.FragmentPlaylistsBinding
import com.practicum.playlistmaker.playlist.ui.PlaylistFragment
import com.practicum.playlistmaker.search.domain.models.Playlist
import org.koin.androidx.viewmodel.ext.android.viewModel


class PlaylistsFragment : Fragment() {
    companion object {
        fun newInstance() = PlaylistsFragment()
    }

    private lateinit var binding: FragmentPlaylistsBinding
    private val viewModel by viewModel<PlaylistsViewModel>()

    private var playlistAdapter = PlaylistsAdapter { selectPlaylistHandler(it) }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPlaylistsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnNewPlaylist.setOnClickListener {
            findNavController().navigate(R.id.action_mediaLibraryFragment_to_newPlaylistFragment)
        }

        binding.recyclerView.layoutManager = GridLayoutManager(requireContext(), 2)

        viewModel.observePlaylists().observe(viewLifecycleOwner) {
            renderActivity(it)
        }

        binding.recyclerView.adapter = playlistAdapter

        viewModel.loadPlaylists()
    }

    private fun selectPlaylistHandler(playlist: Playlist) {
        val bundle = Bundle().apply {
            putLong(PlaylistFragment.PLAYLIST_ID, playlist.id)
        }

        findNavController().navigate(R.id.action_mediaLibraryFragment_to_playlistFragment2, bundle)
    }

    private fun showEmpty() {
        binding.apply {
            placeholderEmpty.visibility = View.VISIBLE
            recyclerView.visibility = View.GONE
        }
    }

    private fun showContent(list: List<Playlist>) {
        binding.apply {
            placeholderEmpty.visibility = View.GONE
            recyclerView.visibility = View.VISIBLE
        }

        playlistAdapter.setList(list)
    }

    private fun renderActivity(list: List<Playlist>) {
        if (list.isEmpty()) {
            showEmpty()
        } else {
            showContent(list)
        }
    }
}
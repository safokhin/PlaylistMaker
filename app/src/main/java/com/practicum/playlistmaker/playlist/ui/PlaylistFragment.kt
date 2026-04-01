package com.practicum.playlistmaker.playlist.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.FragmentPlaylistBinding
import com.practicum.playlistmaker.player.ui.PlayerFragment
import com.practicum.playlistmaker.search.domain.models.Playlist
import com.practicum.playlistmaker.search.domain.models.Track
import com.practicum.playlistmaker.search.ui.TrackAdapter
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import java.io.File

class PlaylistFragment : Fragment() {
    private val id: Long by lazy {
        arguments?.getLong(PLAYLIST_ID)!!
    }

    private lateinit var binding: FragmentPlaylistBinding

    private val viewModel by viewModel<PlaylistViewModel>()

    private var trackAdapter = PlaylistTrackAdapter(
        onClick = { selectTrackHandler(it) },
        onLongClick = { selectLongTrackHandler(it) }
    )

    private lateinit var bottomSBTracks: BottomSheetBehavior<LinearLayout>
    private lateinit var bottomSBMenu: BottomSheetBehavior<LinearLayout>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPlaylistBinding.inflate(inflater, container, false)

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.loadPlaylist(id)

        viewModel.observePlaylist().observe(viewLifecycleOwner) {
            render(it)
        }

        bottomSBTracks = BottomSheetBehavior.from(binding.playlistsBS)
        bottomSBMenu = BottomSheetBehavior.from(binding.playlistsActionsBS)
        bottomSBMenu.state = BottomSheetBehavior.STATE_HIDDEN

        binding.playlistDots.setOnClickListener {
            bottomSBMenu.state = BottomSheetBehavior.STATE_COLLAPSED
            bottomSBTracks.state = BottomSheetBehavior.STATE_HIDDEN
        }

        bottomSBTracks.addBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                binding.overlay.visibility =
                    if (newState == BottomSheetBehavior.STATE_HIDDEN) View.GONE
                    else View.VISIBLE
            }
            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                binding.overlay.alpha = slideOffset.coerceIn(0f, 1f)
            }
        })

        bottomSBMenu.addBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                    bottomSBTracks.state = BottomSheetBehavior.STATE_COLLAPSED
                }

                binding.overlay.visibility =
                    if (newState == BottomSheetBehavior.STATE_HIDDEN) View.GONE
                    else View.VISIBLE
            }
            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                binding.overlay.alpha = slideOffset.coerceIn(0f, 1f)
            }
        })

        binding.btnBack.setOnClickListener { findNavController().popBackStack() }
        binding.playlistShape.setOnClickListener { shareHandler() }
        binding.btnSharePlaylist.setOnClickListener { shareHandler() }
        binding.btnRemovePlaylist.setOnClickListener {
            val playlistName = viewModel.getPlaylist().name

            MaterialAlertDialogBuilder(requireContext())
                .setTitle(getString(R.string.want_remove_playlist, playlistName))
                .setNegativeButton(getString(R.string.no)) { _, _ ->

                }.setPositiveButton(getString(R.string.yes)) { _, _ ->
                    viewModel.removePlaylist()
                    findNavController().popBackStack()
                }.show()
        }

        binding.btnEditPlaylist.setOnClickListener {
            val bundle = Bundle().apply {
                putLong(PLAYLIST_ID, viewModel.getPlaylist().id)
            }
            findNavController().navigate(R.id.action_playlistFragment2_to_editPlaylistFragment, bundle)
        }

        binding.recyclerView.adapter = trackAdapter
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
    }

    private fun selectLongTrackHandler(track: Track) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(resources.getString(R.string.remove_track))
            .setNegativeButton(resources.getString(R.string.yes)) { dialog, which ->
                viewModel.removeTrack(track)
            }.setPositiveButton(resources.getString(R.string.no)) { dialog, which ->
            }.show()
    }

    private fun selectTrackHandler(track: Track) {
        findNavController().navigate(R.id.action_playlistFragment2_to_playerFragment,
            bundleOf(PlayerFragment.EXTRA_TRACK_KEY to track))
    }

    private fun shareHandler() {
        if (trackAdapter.list.isEmpty()) {
            Toast.makeText(
                requireContext(),
                getString(R.string.empty_tracks_in_playlist),
                Toast.LENGTH_LONG
            ).show()
        } else {
            lifecycleScope.launch {
                sharePlaylist(createShareMessage())
            }
        }
    }

    private fun sharePlaylist(message: String) {
        val intent = Intent(Intent.ACTION_SEND)
        intent.type = "text/plain"
        intent.putExtra(Intent.EXTRA_TEXT, message)
        val intentChooser = Intent.createChooser(intent, requireContext().getString(R.string.share_apk))
        intentChooser.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        requireContext().startActivity(intentChooser)
    }

    private fun createShareMessage(): String {
        val playlist = viewModel.getPlaylist()
        val tracksCount = resources.getQuantityString(
            R.plurals.playlist_count,
            playlist.tracks.size,
            playlist.tracks.size
        )

        var message = ""

        message += "${playlist.name}\n"

        if (playlist.description.isNotEmpty()) {
            message += "${playlist.description}\n"
        }

        message += "$tracksCount\n"

        playlist.tracks.forEachIndexed { index, track ->
            message += "${index + 1}. ${track.artistName} - ${track.trackName} (${track.trackTime})\n"
        }

        return message
    }

    private fun render(data: CurrentPlaylistState) {
        binding.apply {
            playlistItem.playlistItemTrackName.text = data.name
            playlistTitle.text = data.name

            playlistDescription.isVisible = data.description.isNotEmpty()
            playlistDescription.text = data.description
            playlistTime.text = context?.resources?.getQuantityString(
                R.plurals.minutes_count,
                data.durationMinutes,
                data.durationMinutes
            )

            playlistItem.playlistItemTrackCount.text =
                context?.resources?.getQuantityString(
                    R.plurals.playlist_count,
                    data.tracks.size,
                    data.tracks.size
                )

            trackCount.text =
                context?.resources?.getQuantityString(
                    R.plurals.playlist_count,
                    data.tracks.size,
                    data.tracks.size
                )
        }

        Glide.with(this)
            .load(Uri.fromFile(File(data.uri ?: "")))
            .placeholder(R.drawable.track_placeholder_icon)
            .transform(CenterCrop())
            .into(binding.playlistImg)

        Glide.with(this)
            .load(Uri.fromFile(File(data.uri ?: "")))
            .placeholder(R.drawable.track_placeholder_icon)
            .transform(CenterCrop())
            .into(binding.playlistItem.playlistItemTrackImg)


        trackAdapter.setList(data.tracks)
        binding.textViewNoTracks.isVisible = data.tracks.isEmpty()
        binding.recyclerView.isVisible = data.tracks.isNotEmpty()
    }

    companion object {
        const val PLAYLIST_ID = "playlist_id"
    }
}
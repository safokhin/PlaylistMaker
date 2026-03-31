package com.practicum.playlistmaker.player.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.FragmentPlayerBinding
import com.practicum.playlistmaker.search.domain.models.Playlist
import com.practicum.playlistmaker.search.domain.models.Track
import com.practicum.playlistmaker.utils.Converter
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import kotlin.getValue

class PlayerFragment : Fragment() {
    private lateinit var binding: FragmentPlayerBinding

    private val viewModel: PlayerViewModel by viewModel {
        parametersOf(getTrack())
    }

    private val viewModelBS: PlaylistBSViewModel by viewModel()
    private var playlistAdapter = PlaylistBSAdapter { selectPlaylistHandler(it) }

    private lateinit var bottomSheetBehavior: BottomSheetBehavior<LinearLayout>


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPlayerBinding.inflate(inflater, container, false)

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val bottomSheetContainer = binding.playlistsBS
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheetContainer).apply {
            state = BottomSheetBehavior.STATE_HIDDEN
        }

        binding.addPlaylistButton.setOnClickListener {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        }

        bottomSheetBehavior.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                binding.overlay.visibility =
                    if (newState == BottomSheetBehavior.STATE_HIDDEN) View.GONE
                    else View.VISIBLE
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                binding.overlay.alpha = slideOffset.coerceIn(0f, 1f)
            }
        })

        binding.btnNewPlaylist.setOnClickListener {
            findNavController().navigate(R.id.action_playerFragment_to_newPlaylistFragment)
        }

        val track = getTrack()

        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.recyclerView.adapter = playlistAdapter
        viewModelBS.loadPlaylists()
        viewModelBS.observePlaylistBSLiveData().observe(viewLifecycleOwner) {
            renderPlaylists(it)
        }

        viewModel.observePlayer().observe(viewLifecycleOwner) {
            changePlayerIcon(it.isPlay)
            enableButton(!it.disableButton)
            changeFavoriteIcon(it.track.isFavorite)
            binding.timePlayer.text = it.progressTime
        }

        binding.playerControl.setOnClickListener {
            viewModel.playbackControl()
        }

        binding.favoriteButton.setOnClickListener {
            viewModel.favoriteHandler()
        }

        binding.btnBack.setOnClickListener {
            findNavController().popBackStack()
        }

        setTrackData(track)
        viewModel.loadIsFavorite()
    }

    override fun onPause() {
        super.onPause()
        viewModel.onPause()
    }

    private fun getTrack(): Track {
        return requireArguments().getParcelable(EXTRA_TRACK_KEY)!!
    }

    private fun showPlaylists(playlists: List<Playlist>) {
        playlistAdapter.setList(playlists)
    }

    private fun selectPlaylistHandler(playlist: Playlist) {
        val isContainsTrack = viewModelBS.addTrackInPlaylist(viewModel.getTrack(), playlist)

        bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN

        if (isContainsTrack) {
            Toast.makeText(
                requireContext(),
                getString(R.string.playlist_track_add_already, playlist.name),
                Toast.LENGTH_LONG
            ).show()
        } else {
            Toast.makeText(
                requireContext(),
                getString(R.string.playlist_track_add, playlist.name),
                Toast.LENGTH_LONG
            ).show()
        }
    }

    private fun setTrackData(track: Track) {
        binding.apply {
            trackNamePlayer.text = track.trackName
            artistNamePlayer.text = track.artistName
            durationValuePlayer.text = track.trackTime
            albumValuePlayer.text = track.collectionName
            yearValuePlayer.text = track.releaseYear ?: ""
            genreValuePlayer.text = track.primaryGenreName
            countryValuePlayer.text = track.country
        }


        val roundedVal: Float = resources.getDimension(R.dimen.track_image_border_px)

        Glide.with(requireContext())
            .load(track.artworkUrl100.replaceAfterLast('/',"512x512bb.jpg"))
            .placeholder(R.drawable.track_placeholder_icon)
            .transform(RoundedCorners(Converter.dpToPx(roundedVal, requireContext())))
            .into(binding.trackImg)
    }

    /** Отрисовка кнопки */
    private fun changePlayerIcon(isPlay: Boolean) {
        if(isPlay) {
            binding.playerControl.setImageResource(R.drawable.button_stop)
        } else {
            binding.playerControl.setImageResource(R.drawable.button_play)
        }
    }

    private fun changeFavoriteIcon(isFavorite: Boolean) {
        if(isFavorite) {
            binding.favoriteButton.setImageResource(R.drawable.button_like)
        } else {
            binding.favoriteButton.setImageResource(R.drawable.button_1)
        }
    }

    private fun renderPlaylists(state: PlaylistBSState) {
        when(state) {
            is PlaylistBSState.Content -> showPlaylists(state.playlists)
        }
    }

    /**
     * Активация кнопки
     * Пока не прогрузился плеер кнопка не доступна
     */
    private fun enableButton(isEnabled: Boolean) {
        binding.playerControl.isEnabled = isEnabled
    }

    companion object {
        const val EXTRA_TRACK_KEY = "extra_track"
    }
}
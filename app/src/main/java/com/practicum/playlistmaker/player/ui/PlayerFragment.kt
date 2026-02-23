package com.practicum.playlistmaker.player.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.FragmentPlayerBinding
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

        val track = getTrack()

        viewModel.observePlayer().observe(viewLifecycleOwner) {
            changePlayerIcon(it.isPlay)
            enableButton(!it.disableButton)
            binding.timePlayer.text = it.progressTime
        }

        binding.playerControl.setOnClickListener {
            viewModel.playbackControl()
        }

        binding.btnBack.setOnClickListener {
            findNavController().popBackStack()
        }

        setTrackData(track)
    }

    override fun onPause() {
        super.onPause()
        viewModel.onPause()
    }

    private fun getTrack(): Track {
        return requireArguments().getParcelable(EXTRA_TRACK_KEY)!!
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
package com.practicum.playlistmaker.new_playlist.ui

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.utils.Converter
import org.koin.androidx.viewmodel.ext.android.viewModel

class EditPlaylistFragment: NewPlaylistFragment() {

     private val id: Long by lazy {
         arguments?.getLong(PLAYLIST_ID)!!
     }
    override val viewModel by viewModel<EditPlaylistViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnCreate.text = getString(R.string.save)
        binding.btnBack.title = getString(R.string.edit)

        binding.btnBack.setOnClickListener { findNavController().popBackStack() }
        requireActivity().onBackPressedDispatcher.addCallback(object: OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                findNavController().popBackStack()
            }
        })

        binding.btnCreate.setOnClickListener {
            val playlistUri = viewModel.getUri()
            var absolutePath = ""

            if (playlistUri != null) {
                absolutePath = saveImageToPrivateStorage(uri = playlistUri)
            }

            viewModel.editPlaylist(id, absolutePath)
            Toast.makeText(
                requireContext(),
                getString(R.string.playlist_edit),
                Toast.LENGTH_LONG)
                .show()
            findNavController().popBackStack()
        }

        viewModel.loadPlaylistInfo(id)

        viewModel.observePlaylist().observe(viewLifecycleOwner) {
            val roundedVal: Float = resources.getDimension(R.dimen.radius_sm)

            it.uri?.let { uri ->
                Glide.with(this)
                    .load(uri)
                    .transform(CenterCrop(), RoundedCorners(Converter.dpToPx(roundedVal, this.requireContext())))
                    .into(binding.playlistAddPhoto)
            }

            if (binding.editTextName.editText?.text.toString() != it.name) {
                binding.editTextName.editText?.setText(it.name)
            }

            if (binding.editTextDescription.editText?.text.toString() != it.description) {
                binding.editTextDescription.editText?.setText(it.description)
            }
        }
    }

    companion object {
        const val PLAYLIST_ID = "playlist_id"
    }
}
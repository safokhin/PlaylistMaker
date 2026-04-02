package com.practicum.playlistmaker.new_playlist.ui

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.FragmentNewPlaylistBinding
import com.practicum.playlistmaker.utils.Converter
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.File
import java.io.FileOutputStream

open class NewPlaylistFragment : Fragment() {
    protected lateinit var binding: FragmentNewPlaylistBinding
    protected open val viewModel by viewModel<NewPlaylistViewModel>()

    lateinit var backDialog: MaterialAlertDialogBuilder

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentNewPlaylistBinding.inflate(inflater, container, false)

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        backDialog = initBackDialog()

        viewModel.observePlaylist().observe(viewLifecycleOwner) {
            binding.btnCreate.isEnabled = !it.name.trim().isEmpty()

            val roundedVal: Float = resources.getDimension(R.dimen.radius_sm)

            it.uri?.let { uri ->

                Glide.with(this)
                    .load(uri)
                    .transform(CenterCrop(), RoundedCorners(Converter.dpToPx(roundedVal, this.requireContext())))
                    .into(binding.playlistAddPhoto)
            }
        }

        val pickMedia = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            uri?.let {
                viewModel.changeImg(uri)
            }
        }


        binding.btnBack.setOnClickListener {
            if(viewModel.isStartCreating()) {
                backDialog.show()
            } else {
                findNavController().popBackStack()
            }
        }

        requireActivity().onBackPressedDispatcher.addCallback(object: OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if(viewModel.isStartCreating()) {
                    backDialog.show()
                } else {
                    findNavController().popBackStack()
                }
            }
        })

        binding.editTextName.editText?.doOnTextChanged { text, _, _, _ ->
            viewModel.changeName(text.toString())
        }

        binding.editTextDescription.editText?.doOnTextChanged { text, start, before, count ->
            viewModel.changeDescription(text.toString())
        }

        binding.playlistAddPhoto.setOnClickListener {
            pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }

        binding.btnCreate.setOnClickListener {
            val playlistUri = viewModel.getUri()
            var absolutePath = ""

            if (playlistUri != null) {
                absolutePath = saveImageToPrivateStorage(uri = playlistUri)
            }

            viewModel.createPlaylist(absolutePath)
            findNavController().popBackStack()
            Toast.makeText(
                requireContext(),
                getString(R.string.playlist_name_created, viewModel.getName()),
                Toast.LENGTH_LONG
            ).show()
        }

        binding.btnCreate.isEnabled = false
    }

    protected fun initBackDialog(): MaterialAlertDialogBuilder {
        return MaterialAlertDialogBuilder(requireContext())
            .setTitle(resources.getString(R.string.finish_creating_playlist))
            .setMessage(resources.getString(R.string.all_unsaved_data_will_be_lost))
            .setNegativeButton(resources.getString(R.string.cancel)) { dialog, which ->

            }.setPositiveButton(resources.getString(R.string.finish)) { dialog, which ->
                findNavController().popBackStack()
            }
    }

    protected fun saveImageToPrivateStorage(uri: Uri): String {
        // Создаём экземпляр класса File, который указывает на нужный каталог
        val filePath = File(requireActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES), "myalbum")

        //создаем каталог, если он не создан
        if (!filePath.exists()){
            filePath.mkdirs()
        }

        //создаём экземпляр класса File, который указывает на файл внутри каталога
        val file = File(filePath, "test_${System.currentTimeMillis()}.jpg")
        // создаём входящий поток байтов из выбранной картинки
        val inputStream = requireActivity().contentResolver.openInputStream(uri)
        // создаём исходящий поток байтов в созданный выше файл
        val outputStream = FileOutputStream(file)

        // записываем картинку с помощью BitmapFactory
        BitmapFactory
            .decodeStream(inputStream)
            .compress(Bitmap.CompressFormat.JPEG, 30, outputStream)

        return file.absolutePath
    }
}
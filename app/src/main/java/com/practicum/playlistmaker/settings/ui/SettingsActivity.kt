package com.practicum.playlistmaker.settings.ui

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.ActivitySettingsBinding
import org.koin.androidx.viewmodel.ext.android.viewModel

class SettingsActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySettingsBinding
    private val viewModel by viewModel<SettingsViewModel>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        viewModel.observeIsDark().observe(this) {
            binding.switcherTheme.isChecked = it
        }

        binding.btnBack.setNavigationOnClickListener {
            finish()
        }

        binding.btnShare.setOnClickListener {
            viewModel.shareApp()
        }

        binding.btnSupport.setOnClickListener {
            viewModel.openSupport()
        }

        binding.btnAgree.setOnClickListener {
            viewModel.openTerms()
        }

        binding.switcherTheme.setOnCheckedChangeListener { switcher, checked ->
            viewModel.setDarkTheme(checked)
        }
    }
}
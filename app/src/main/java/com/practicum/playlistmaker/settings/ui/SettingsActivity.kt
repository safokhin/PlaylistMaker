package com.practicum.playlistmaker.settings.ui

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.ActivitySettingsBinding

class SettingsActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySettingsBinding
    private var viewModel: SettingsViewModel? = null

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

        viewModel = ViewModelProvider(this, SettingsViewModel.getFactory())
            .get(SettingsViewModel::class.java)

        binding.btnBack.setNavigationOnClickListener {
            finish()
        }

        binding.btnShare.setOnClickListener {
            viewModel?.shareApp()
        }

        binding.btnSupport.setOnClickListener {
            viewModel?.openSupport()
        }

        binding.btnAgree.setOnClickListener {
            viewModel?.openTerms()
        }


        val switcherTheme = binding.switcherTheme
        switcherTheme.isChecked = viewModel?.isDarkTheme() ?: false
        switcherTheme.setOnCheckedChangeListener { switcher, checked ->
            viewModel?.setDarkTheme(checked)
        }
    }
}
package com.practicum.playlistmaker.ui.settings

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.switchmaterial.SwitchMaterial
import com.practicum.playlistmaker.Creator
import com.practicum.playlistmaker.R

class SettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_settings)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val settingsInteractor = Creator.provideSettingsInteractor(this)

        findViewById<MaterialToolbar>(R.id.btn_back).setNavigationOnClickListener {
            finish()
        }

        findViewById<Button>(R.id.btn_share).setOnClickListener {
            val intent = Intent(Intent.ACTION_SEND)
            intent.type = "text/plain"
            intent.putExtra(Intent.EXTRA_TEXT, getString(R.string.link_course))
            startActivity(intent)
        }

        findViewById<Button>(R.id.btn_support).setOnClickListener {
            val intent = Intent(Intent.ACTION_SENDTO)
            intent.data = "mailto:".toUri()
            intent.putExtra(Intent.EXTRA_EMAIL, arrayOf(getString(R.string.email)))
            intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.email_subject))
            intent.putExtra(Intent.EXTRA_TEXT, getString(R.string.email_body))
            startActivity(intent)
        }

        findViewById<Button>(R.id.btn_agree).setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, getString(R.string.link_agree).toUri())
            startActivity(intent)
        }

        val switcherTheme = findViewById<SwitchMaterial>(R.id.switcherTheme)
        switcherTheme.isChecked = settingsInteractor.isDarkTheme()
        switcherTheme.setOnCheckedChangeListener { switcher, checked ->
            settingsInteractor.setDarkTheme(checked)
        }
    }
}
package com.practicum.playlistmaker

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.appbar.MaterialToolbar
import androidx.core.net.toUri

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

        val buttonBack = findViewById<MaterialToolbar>(R.id.btn_back)
        buttonBack.setNavigationOnClickListener {
            finish()
        }

        val buttonShare = findViewById<Button>(R.id.btn_share)
        buttonShare.setOnClickListener {
            val intent = Intent(Intent.ACTION_SEND)
            intent.type = "text/plain"
            intent.putExtra(Intent.EXTRA_TEXT, getString(R.string.link_course))
            startActivity(intent)
        }

        val buttonSupport = findViewById<Button>(R.id.btn_support)
        buttonSupport.setOnClickListener {
            val intent = Intent(Intent.ACTION_SENDTO)
            intent.data = "mailto:".toUri()
            intent.putExtra(Intent.EXTRA_EMAIL, arrayOf(R.string.email))
            intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.email_subject))
            intent.putExtra(Intent.EXTRA_TEXT, getString(R.string.email_body))
            startActivity(intent)
        }

        val buttonAgree = findViewById<Button>(R.id.btn_agree)
        buttonAgree.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, getString(R.string.link_agree).toUri())
            startActivity(intent)
        }
    }
}
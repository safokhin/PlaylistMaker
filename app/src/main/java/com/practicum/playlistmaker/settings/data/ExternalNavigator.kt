package com.practicum.playlistmaker.settings.data

import android.content.Context
import android.content.Intent
import androidx.core.net.toUri
import com.practicum.playlistmaker.R

class ExternalNavigator(private val context: Context) {

    fun shareApp() {
        val intent = Intent(Intent.ACTION_SEND)
        intent.type = "text/plain"
        intent.putExtra(Intent.EXTRA_TEXT, context.getString(R.string.link_course))
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(intent)
    }

    fun openSupport() {
        val intent = Intent(Intent.ACTION_SENDTO)
        intent.data = "mailto:".toUri()
        intent.putExtra(Intent.EXTRA_EMAIL, arrayOf(context.getString(R.string.email)))
        intent.putExtra(Intent.EXTRA_SUBJECT, context.getString(R.string.email_subject))
        intent.putExtra(Intent.EXTRA_TEXT, context.getString(R.string.email_body))
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(intent)
    }

    fun openTerms() {
        val intent = Intent(Intent.ACTION_VIEW, context.getString(R.string.link_agree).toUri())
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(intent)
    }
}
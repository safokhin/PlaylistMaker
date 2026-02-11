package com.practicum.playlistmaker.utils

import android.content.Context
import android.util.TypedValue

object Converter {
    fun dateToYear(value: String?): String? {
        return value?.take(4)
    }

    fun dpToPx(dp: Float, context: Context): Int {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, context.resources.displayMetrics).toInt()
    }
}


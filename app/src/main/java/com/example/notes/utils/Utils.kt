package com.example.notes.utils

import android.content.Context
import android.util.TypedValue
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class Utils {
    companion object {
        fun getTimeStamp(): String {
            return SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())
        }

        fun convertDpToPixel(dp: Float, context: Context): Float {
            return TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, dp, context.resources.displayMetrics
            )
        }

        fun formatDateTimeToDisplay(timestamp: String): String {
            val inputFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
            val outputFormat = SimpleDateFormat("hh:mm a, dd MMMM yyyy", Locale.getDefault())
            val date = inputFormat.parse(timestamp)
            return outputFormat.format(date!!)
        }
    }
}
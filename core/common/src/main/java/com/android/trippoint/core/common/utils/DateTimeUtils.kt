package com.android.trippoint.core.common.utils

import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone

object DateTimeUtils {
    private val isoParser = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US).apply {
        timeZone = TimeZone.getTimeZone("UTC")
    }
    private val displayFormatter = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault())

    fun formatIsoToDisplay(isoString: String?): String {
        if (isoString == null) return ""
        return try {
            val date = isoParser.parse(isoString)
            if (date != null) {
                displayFormatter.format(date)
            } else {
                isoString
            }
        } catch (e: Exception) {
            isoString
        }
    }
}

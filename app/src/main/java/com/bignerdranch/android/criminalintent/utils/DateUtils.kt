package com.bignerdranch.android.criminalintent.utils

import java.util.*

fun getDateString(date: Date?): String {
    if(date == null) {
        return ""
    }

    val calendar = Calendar.getInstance()
    calendar.time = date

    val dayOfWeek = calendar.getDisplayName(
        Calendar.DAY_OF_WEEK,
        Calendar.SHORT,
        Locale.getDefault()
    )

    val formattedDate =
        java.text.DateFormat
            .getDateTimeInstance(
                java.text.DateFormat.MEDIUM,
                java.text.DateFormat.SHORT
            )
            .format(date)

    return "$dayOfWeek, $formattedDate"
}
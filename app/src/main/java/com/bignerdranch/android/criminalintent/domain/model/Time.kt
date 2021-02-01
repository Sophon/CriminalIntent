package com.bignerdranch.android.criminalintent.domain.model

import java.io.Serializable
import java.util.*

class Time(
    private val hour: Int,
    private val minute: Int
): Serializable {

    companion object {
        fun dateToTime(date: Date): Time {
            val calendar = Calendar.getInstance()
            calendar.time = date

            return Time(
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE)
            )
        }

        fun addTimeToDate(date: Date, time: Time): Date {
            val calendar = Calendar.getInstance().apply {
                this.time = date
            }

            calendar.set(Calendar.HOUR_OF_DAY, time.hour)
            calendar.set(Calendar.MINUTE, time.minute)

            return calendar.time
        }
    }
}
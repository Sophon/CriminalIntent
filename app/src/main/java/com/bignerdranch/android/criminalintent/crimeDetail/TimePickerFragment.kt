package com.bignerdranch.android.criminalintent.crimeDetail

import android.app.Dialog
import android.app.TimePickerDialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import com.bignerdranch.android.criminalintent.domain.model.Time
import java.util.*

private const val ARG_TIME = "time"

class TimePickerFragment: DialogFragment() {

    private lateinit var date: Date

    interface Callbacks {
        fun onTimeSelected(date: Date)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        date = arguments?.getSerializable(ARG_TIME) as Date
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val calendar = Calendar.getInstance().apply {
            time = date
        }

        val initialHour = calendar.get(Calendar.HOUR_OF_DAY)
        val initialMinute = calendar.get(Calendar.MINUTE)

        return TimePickerDialog(
            requireContext(),
            createTimeListener(),
            initialHour,
            initialMinute,
            true
        )
    }

    private fun createTimeListener(): TimePickerDialog.OnTimeSetListener {
        return TimePickerDialog.OnTimeSetListener { _, hourOfDay, minute ->
            targetFragment?.let { fragment ->
                (fragment as Callbacks)
                    .onTimeSelected(
                        Time.addTimeToDate(date, Time(hourOfDay, minute))
                    )
            }
        }
    }

    companion object {
        fun getInstance(date: Date): TimePickerFragment {
            val args = Bundle().apply {
                putSerializable(ARG_TIME, date)
            }

            return TimePickerFragment().apply {
                arguments = args
            }
        }
    }
}
package com.bignerdranch.android.criminalintent.ui

import android.app.Dialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.widget.TimePicker
import androidx.fragment.app.DialogFragment
import com.bignerdranch.android.criminalintent.model.Time
import java.util.*

private const val ARG_TIME = "time"

class TimePickerFragment: DialogFragment() {

    interface Callbacks {
        fun onTimeSelected(time: Time)
    }

    //==========

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val timeListener = TimePickerDialog.OnTimeSetListener {
                _: TimePicker, hour: Int, minute: Int ->

            val resultTime =
                Time(hour, minute)

            targetFragment?.let { fragment ->
                (fragment as Callbacks).onTimeSelected(resultTime)
            }
        }

        val calendar = Calendar.getInstance()
        val initialHour = calendar.get(Calendar.HOUR_OF_DAY)
        val initialMinute = calendar.get(Calendar.MINUTE)

        return TimePickerDialog(
            requireContext(),
            timeListener,
            initialHour,
            initialMinute,
            true
        )
    }

    //==========

    companion object {
        fun newInstance(time: Time): TimePickerFragment {
            val argBundle = Bundle().apply {
                putSerializable(ARG_TIME, time)
            }

            return TimePickerFragment()
                .apply {
                arguments = argBundle
            }
        }
    }
}
package com.bignerdranch.android.criminalintent.crimeDetail

import android.app.DatePickerDialog
import android.app.Dialog
import android.os.Bundle
import android.widget.DatePicker
import androidx.fragment.app.DialogFragment
import java.util.*

private const val ARG_DATE = "date"
private const val DIALOG_TIME = "dialog time"
private const val REQUEST_TIME = 0

class DatePickerFragment:
    TimePickerFragment.Callbacks,
    DialogFragment()
{

    private lateinit var date: Date

    interface Callbacks {
        fun onDateSelected(date: Date)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        date = arguments?.getSerializable(ARG_DATE) as Date
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val calendar = Calendar.getInstance().apply {
            time = date
        }
        val initialYear = calendar.get(Calendar.YEAR)
        val initialMonth = calendar.get(Calendar.MONTH)
        val initialDay = calendar.get(Calendar.DAY_OF_MONTH)

        return DatePickerDialog(
            requireContext(),
            createDateListener(),
            initialYear,
            initialMonth,
            initialDay
        )
    }

    private fun createDateListener(): DatePickerDialog.OnDateSetListener  {
        return DatePickerDialog.OnDateSetListener { _: DatePicker, year: Int, month: Int, day: Int ->
            val selectedDate = GregorianCalendar(year, month, day).time

            TimePickerFragment.getInstance(selectedDate).apply {
                setTargetFragment(this@DatePickerFragment, REQUEST_TIME)
                show(this@DatePickerFragment.parentFragmentManager, DIALOG_TIME)
            }
        }
    }

    override fun onTimeSelected(date: Date) {
        targetFragment?.let { fragment ->
            (fragment as Callbacks).onDateSelected(date)
        }
    }

    companion object {
        fun getInstance(date: Date): DatePickerFragment {
            val args = Bundle().apply {
                putSerializable(ARG_DATE, date)
            }

            return DatePickerFragment().apply {
                arguments = args
            }
        }
    }
}
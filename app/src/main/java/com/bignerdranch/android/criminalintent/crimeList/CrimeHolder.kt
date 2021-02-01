package com.bignerdranch.android.criminalintent.crimeList

import android.content.Context
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.example.listbrowser.R
import com.example.listbrowser.databinding.ItemCrimeBinding
import com.bignerdranch.android.criminalintent.domain.model.Crime
import java.text.SimpleDateFormat
import java.util.*

class CrimeHolder(
    private val context: Context,
    private val binding: ItemCrimeBinding,
    private val onCrimeClicked: (UUID) -> Unit
): RecyclerView.ViewHolder(binding.root), View.OnClickListener {

    private lateinit var crime: Crime

    init {
        itemView.setOnClickListener(this)
    }

    fun bind(crime: Crime) {
        this.crime = crime
        binding.txtCrimeTitle.text = crime.title
        binding.txtCrimeDate.text = SimpleDateFormat(
            context.getString(R.string.crime_date_and_time_format),
            Locale.getDefault()
        ).format(crime.date)
        binding.imgCrimeSolved.visibility = if(crime.isSolved) {
            View.VISIBLE
        } else {
            View.INVISIBLE
        }
    }

    override fun onClick(v: View?) {
        onCrimeClicked.invoke(crime.id)
    }
}
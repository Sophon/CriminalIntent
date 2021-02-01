package com.bignerdranch.android.criminalintent.viewModel

import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import com.bignerdranch.android.criminalintent.domain.model.Crime
import com.bignerdranch.android.criminalintent.utils.getDateString

class CrimeViewModel(private val crimeClickedFunction: (Crime) -> Unit = {}): BaseObservable() {

    fun onCrimeClicked() = crimeClickedFunction(crime!!)

    var crime: Crime? = null
        set(crime) {
            field = crime
            notifyChange()
        }

    @get:Bindable
    val dateString: String?
        get() = getDateString(crime?.date)
}
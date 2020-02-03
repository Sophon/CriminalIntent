package com.bignerdranch.android.criminalintent.viewModel

import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import com.bignerdranch.android.criminalintent.model.Crime
import com.bignerdranch.android.criminalintent.utils.getDateString

class CrimeViewModel: BaseObservable() {

    fun onCrimeClicked() {

    }

    var crime: Crime? = null
        set(crime) {
            field = crime
            notifyChange()
        }

    @get:Bindable
    val dateString: String?
        get() = getDateString(crime?.date)
}
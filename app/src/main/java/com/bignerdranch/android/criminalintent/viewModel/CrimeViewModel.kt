package com.bignerdranch.android.criminalintent.viewModel

import androidx.databinding.BaseObservable
import com.bignerdranch.android.criminalintent.model.Crime
import com.bignerdranch.android.criminalintent.utils.getDateString

class CrimeViewModel(): BaseObservable() {

    var crime: Crime? = null
        set(crime) {
            field = crime
            notifyChange()
        }

    val dateString: String?
        get() = getDateString(crime?.date)
}
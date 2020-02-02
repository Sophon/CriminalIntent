package com.bignerdranch.android.criminalintent.ViewModel

import androidx.lifecycle.ViewModel
import com.bignerdranch.android.criminalintent.Model.Crime
import com.bignerdranch.android.criminalintent.repository.CrimeRepository

class CrimeListViewModel: ViewModel() {

    private val crimeRepository = CrimeRepository.get()
    val crimeListLiveData = crimeRepository.getCrimes()
    var emptyDialogShown = false

    //==========

    fun addCrime(crime: Crime) {
        crimeRepository.addCrime(crime)
    }
}
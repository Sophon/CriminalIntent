package com.bignerdranch.android.criminalintent.viewModel

import androidx.lifecycle.ViewModel
import com.bignerdranch.android.criminalintent.model.Crime
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
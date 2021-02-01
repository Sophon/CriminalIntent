package com.bignerdranch.android.criminalintent.crimeList

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bignerdranch.android.criminalintent.domain.model.Crime
import kotlinx.coroutines.launch
import kotlin.random.Random

class CrimeListVM: ViewModel() {

    val repo = CrimeRepository.get()
    val crimes = mutableListOf<Crime>()
    val crimesLiveData = MutableLiveData<List<Crime>>()

    init {
        mockCrimes()
        getCrimes()
    }

    private fun getCrimes() {
        viewModelScope.launch {
            repo.getCrimesFlow().collect {
                crimesLiveData.postValue(it)
            }
        }
    }

    private fun mockCrimes() {
        for(i in 0 until 100) {
            val crime = Crime()
            crime.title = "Crime #$i"
            crime.isSolved = Random.nextInt(0, 10) % 3 == 0
            crimes += crime
        }
    }
}
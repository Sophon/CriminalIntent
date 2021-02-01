package com.example.listbrowser.crimeDetail

import androidx.lifecycle.*
import com.example.listbrowser.arch.LiveEvent
import com.example.listbrowser.crimeList.CrimeRepository
import com.bignerdranch.android.criminalintent.domain.model.Crime
import kotlinx.coroutines.launch
import java.util.*

class CrimeDetailVM: ViewModel() {

    private val repo = CrimeRepository.get()

    val cachedCrime = MutableLiveData<Crime>()
    private val cachedCrimeModified = MutableLiveData<Any>()
    private val localCrime = MutableLiveData<Crime?>()
    val canSaveCrime = MediatorLiveData<Boolean>()

    val exitFragmentEvent = LiveEvent<Any>()

    fun getCrimeFromId(crimeId: UUID?) {
        if(crimeId == null) {
            cachedCrime.postValue(Crime())
        } else {
            viewModelScope.launch {
                repo.getCrimeFlow(crimeId).collect { crime ->
                    crime?.apply {
                        localCrime.postValue(crime)
                        cachedCrime.postValue(Crime(crime))
                    }
                }
            }
        }

        canSaveCrime.addSource(cachedCrimeModified) {
            canSaveCrime.value = if(localCrime.value == null) {
                true
            } else {
                val canSave = (cachedCrime.value?.equals(localCrime.value))?.not()
                canSave
            }
        }
    }

    fun saveCrimeToDb() {
        if(canSaveCrime.value == true) {
            viewModelScope.launch {
                repo.addCrime(cachedCrime.value!!)
            }

            exitFragmentEvent.postValue(Any())
        }
    }

    fun deleteCrimeFromDb() {
        localCrime.value?.let { crime ->
            viewModelScope.launch {
                repo.deleteCrime(crime)
            }
        }

        exitFragmentEvent.postValue(Any())
    }

    //region Crime edit
    fun updateCrimeTitle(newTitle: CharSequence?) {
        cachedCrime.value?.title = newTitle.toString()
        cachedCrimeModified.postValue(Any())
    }

    fun updateCrimeDetails(newDetails: CharSequence?) {
        cachedCrime.value?.details = newDetails.toString()
        cachedCrimeModified.postValue(Any())
    }

    fun updateCrimeIsSolved(isSolved: Boolean) {
        cachedCrime.value?.isSolved = isSolved
        cachedCrimeModified.postValue(Any())
    }

    fun updateCrimeDate(date: Date) {
        cachedCrime.value?.date = date
        cachedCrimeModified.postValue(Any())
    }

    fun updateCrimeSuspect(suspect: String) {
        cachedCrime.value?.suspect = suspect
        cachedCrimeModified.postValue(Any())
    }
    //endregion

}
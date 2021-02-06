package com.bignerdranch.android.criminalintent

import android.app.Application
import com.bignerdranch.android.criminalintent.domain.repo.CrimeRepository
import timber.log.Timber

class CriminalIntentApplication: Application() {

    override fun onCreate() {
        super.onCreate()

        Timber.plant(Timber.DebugTree())

        CrimeRepository.initialize(this)
    }
}
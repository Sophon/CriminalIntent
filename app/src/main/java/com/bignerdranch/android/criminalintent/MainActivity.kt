package com.bignerdranch.android.criminalintent

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.bignerdranch.android.criminalintent.crimeDetail.CrimeFragment
import com.bignerdranch.android.criminalintent.crimeList.CrimeListFragment
import java.util.*

class MainActivity:
    AppCompatActivity(),
    CrimeListFragment.Callbacks
{

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        displayCrimeList()
    }

    override fun onCrimeClicked(crimeId: UUID) {
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.frame_main, CrimeFragment.getExistingCrimeInstance(crimeId))
            .addToBackStack(null)
            .commit()
    }

    override fun onAddCrime() {
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.frame_main, CrimeFragment.getNewCrimeInstance())
            .addToBackStack(null)
            .commit()
    }

    private fun displayCrimeList() {
        if(supportFragmentManager.findFragmentById(R.id.frame_main) == null) {
            supportFragmentManager
                .beginTransaction()
                .add(R.id.frame_main, CrimeListFragment.getInstance())
                .commit()
        }
    }
}
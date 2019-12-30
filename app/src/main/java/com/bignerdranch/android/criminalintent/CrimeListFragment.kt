package com.bignerdranch.android.criminalintent

import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.RecyclerView

class CrimeListFragment: Fragment() {

    private lateinit var crimeRecyclerView: RecyclerView
    private val crimeListViewModel: CrimeListViewModel by lazy {
        ViewModelProviders.of(this).get(crimeListViewModel::class.java)
    }
}
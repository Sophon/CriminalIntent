package com.bignerdranch.android.criminalintent.crimeList

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.bignerdranch.android.criminalintent.databinding.ItemCrimeBinding
import com.bignerdranch.android.criminalintent.domain.model.Crime
import java.util.*

class CrimeAdapter(
    private val context: Context,
    private val onCrimeClicked: (UUID) -> Unit
): ListAdapter<Crime, CrimeHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CrimeHolder {
        val binding = ItemCrimeBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )

        return CrimeHolder(context, binding, onCrimeClicked)
    }

    override fun onBindViewHolder(holder: CrimeHolder, position: Int) {
        holder.bind(getItem(position))
    }
}

private class DiffCallback: DiffUtil.ItemCallback<Crime>() {
    override fun areItemsTheSame(oldItem: Crime, newItem: Crime): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Crime, newItem: Crime): Boolean {
        return oldItem == newItem
    }
}
package com.bignerdranch.android.criminalintent.ui

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bignerdranch.android.criminalintent.CrimeListViewModel
import com.bignerdranch.android.criminalintent.EmptyAlertFragment
import com.bignerdranch.android.criminalintent.Model.Crime
import com.bignerdranch.android.criminalintent.R
import com.bignerdranch.android.criminalintent.databinding.FragmentCrimeListBinding
import java.util.*

private const val TAG = "CrimeListFragment"
private const val DIALOG_EMPTY = "empty"
private const val REQUEST_EMPTY = 0
private const val KEY_DIALOG = "dialog"

class CrimeListFragment:
    Fragment(),
    EmptyAlertFragment.Callbacks {

    interface Callbacks {
        fun onCrimeClicked(crimeId: UUID)
    }

    //==========

    private var callback: Callbacks? = null
//    private lateinit var crimeRecyclerView: RecyclerView
    private lateinit var binding: FragmentCrimeListBinding
    private val crimeListViewModel: CrimeListViewModel by lazy {
        ViewModelProviders.of(this).get(CrimeListViewModel::class.java)
    }

    //==========

    override fun onAttach(context: Context) {
        super.onAttach(context)

        callback = context as Callbacks?
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setHasOptionsMenu(true)
        crimeListViewModel.emptyDialogShown =
            savedInstanceState?.getBoolean(KEY_DIALOG, false) ?: false
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

//        val view = inflater.inflate(R.layout.fragment_crime_list, container, false)
//
//        crimeRecyclerView = view.findViewById(R.id.crime_recycler_view)
//        crimeRecyclerView.layoutManager = LinearLayoutManager(context)
//        crimeRecyclerView.adapter = CrimeListAdapter()

        binding = FragmentCrimeListBinding.inflate(inflater, container, false)

        binding.crimeRecyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = CrimeListAdapter()
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        crimeListViewModel.crimeListLiveData.observe(
            viewLifecycleOwner,
            Observer { crimes ->
                crimes?.let {
                    Log.i(TAG, "Retrieved ${crimes.size} crimes.")
                    if(crimes.isEmpty() && !crimeListViewModel.emptyDialogShown) {
                        showEmptyDialog()
                    }
                    updateUI(crimes)
                }
            }
        )
    }

    override fun onDetach() {
        super.onDetach()

        callback = null
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)

        inflater.inflate(R.menu.fragment_crime_list, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId) {
            R.id.new_crime -> {
                createNewCrime()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        outState.putBoolean(KEY_DIALOG, crimeListViewModel.emptyDialogShown)
    }

    override fun onCreateSelected() = createNewCrime()

    //==========

    private fun updateUI(crimes: List<Crime>) {
//        (crimeRecyclerView.adapter as CrimeListAdapter).submitList(crimes)
        (binding.crimeRecyclerView.adapter as CrimeListAdapter).submitList(crimes)

    }

    private fun showEmptyDialog() {
        EmptyAlertFragment.newInstance()
            .apply {
            setTargetFragment(this@CrimeListFragment,
                REQUEST_EMPTY
            )
            show(this@CrimeListFragment.requireFragmentManager(),
                DIALOG_EMPTY
            )
        }

        crimeListViewModel.emptyDialogShown = true
    }

    private fun createNewCrime() {
        val crime = Crime()
        crimeListViewModel.addCrime(crime)
        callback?.onCrimeClicked(crime.id)
        Log.d(TAG, "new crime added")
    }

    //==========

    companion object {
        fun newInstance(): CrimeListFragment {
            return CrimeListFragment()
        }
    }

    //==========

    private inner class CrimeHolder(view: View)
        : RecyclerView.ViewHolder(view), View.OnClickListener {

        private lateinit var crime: Crime
        private val titleTextView = itemView.findViewById<TextView>(R.id.crime_title)
        private val dateTextView = itemView.findViewById<TextView>(R.id.crime_detail_date)
        private val solvedImageView =
            itemView.findViewById<ImageView>(R.id.crime_detail_solved)

        init {
            itemView.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            callback?.onCrimeClicked(crime.id)
        }

        fun bind(crime: Crime) {
            this.crime = crime

            titleTextView.text = crime.title
            dateTextView.text = getDateString(crime.date)
            solvedImageView.visibility = if(crime.isSolved) {
                View.VISIBLE
            } else {
                View.GONE
            }
        }

        fun getDateString(date: Date): String {
            val calendar = Calendar.getInstance()
            calendar.time = date

            val dayOfWeek = calendar.getDisplayName(
                Calendar.DAY_OF_WEEK,
                Calendar.SHORT,
                Locale.getDefault()
            )

            val formattedDate =
                java.text.DateFormat
                    .getDateTimeInstance(
                        java.text.DateFormat.MEDIUM,
                        java.text.DateFormat.SHORT
                    )
                    .format(date)

            return "$dayOfWeek, $formattedDate"
        }
    }

    private inner class CrimeListAdapter
        : ListAdapter<Crime, CrimeHolder>(DiffCallback()) {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CrimeHolder {
            val view =
                layoutInflater.inflate(R.layout.list_item_crime, parent, false)
            return CrimeHolder(view)
        }

        override fun onBindViewHolder(holder: CrimeHolder, position: Int) {
            holder.bind(getItem(position))
        }
    }

    private inner class DiffCallback: DiffUtil.ItemCallback<Crime>() {

        override fun areItemsTheSame(oldItem: Crime, newItem: Crime): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Crime, newItem: Crime): Boolean {
            return oldItem == newItem
        }
    }
}
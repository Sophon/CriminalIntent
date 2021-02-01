package com.bignerdranch.android.criminalintent.crimeList

import android.content.Context
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.bignerdranch.android.criminalintent.R
import com.bignerdranch.android.criminalintent.databinding.FragmentCrimeListBinding
import com.example.listbrowser.domain.model.Crime
import java.util.*

class CrimeListFragment: Fragment() {

    private lateinit var binding: FragmentCrimeListBinding

    private val vm: CrimeListVM by lazy {
        ViewModelProvider(this).get(CrimeListVM::class.java)
    }

    private var callbacks: Callbacks? = null

    interface Callbacks {
        fun onCrimeClicked(crimeId: UUID)
        fun onAddCrime()
    }

    //region Lifecycle
    override fun onAttach(context: Context) {
        super.onAttach(context)

        callbacks = context as Callbacks?
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCrimeListBinding.inflate(inflater)

        setupRecyclerView()

        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)

        inflater.inflate(R.menu.crime_list, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId) {
            R.id.menu_new_crime -> {
                callbacks?.onAddCrime()
                true
            }
            else -> {
                super.onOptionsItemSelected(item)
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupObservers()
    }

    override fun onDetach() {
        super.onDetach()

        callbacks = null
    }

    //endregion

    //region RecyclerView
    private fun setupRecyclerView() {
        binding.rvList.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = CrimeAdapter(requireContext(), callbacks!!::onCrimeClicked)
        }
    }

    private fun updateList(crimeList: List<Crime>) {
        (binding.rvList.adapter as CrimeAdapter).submitList(crimeList)
    }
    //endregion

    private fun setupObservers() {
        vm.crimesLiveData.observe(
            viewLifecycleOwner,
            { crimes ->
                updateList(crimes)
            }
        )
    }

    companion object {
        fun getInstance(): CrimeListFragment {
            return CrimeListFragment()
        }
    }
}
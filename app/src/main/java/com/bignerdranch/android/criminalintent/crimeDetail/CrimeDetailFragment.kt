package com.bignerdranch.android.criminalintent.crimeDetail

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.ContactsContract
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.text.format.DateFormat
import android.view.*
import android.widget.Toast
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.observe
import com.bignerdranch.android.criminalintent.R
import com.bignerdranch.android.criminalintent.databinding.FragmentCrimeDetailBinding
import java.io.File
import java.util.*

private const val DIALOG_DATE = "dialog date"
private const val ARG_CRIME_ID = "crime id"
private const val DIALOG_IMAGE = "DialogImage"
private const val REQUEST_DATE = 0
private const val REQUEST_CONTACTS = 1
private const val REQUEST_CAMERA = 2
private const val REQUEST_IMAGE = 3


class CrimeDetailFragment:
    DatePickerFragment.Callbacks,
    Fragment()
{

    private lateinit var binding: FragmentCrimeDetailBinding

    private val vm: CrimeDetailVM by lazy {
        ViewModelProvider(this).get(CrimeDetailVM::class.java)
    }

    //region Lifecycle
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setHasOptionsMenu(true)

        vm.getCrimeFromId(arguments?.getSerializable(ARG_CRIME_ID) as UUID?)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCrimeDetailBinding.inflate(inflater)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupEditText()
        setupSolved()
        setupDateButton()
        setupSaveButton()
        setupSuspectButton()
        setupReportButton()
        setupImageButton()

        setupObservers()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)

        inflater.inflate(R.menu.crime_detail, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId) {
            R.id.menu_delete_crime -> {
                Toast.makeText(
                    requireContext(),
                    R.string.fragment_crime_toast_deleted,
                    Toast.LENGTH_SHORT
                ).show()

                vm.deleteCrimeFromDb()

                true
            } else -> {
                super.onOptionsItemSelected(item)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when {
            resultCode != Activity.RESULT_OK -> return

            requestCode == REQUEST_CONTACTS && data != null -> {
                val cursor = data.data?.let { contactUri ->
                    requireActivity().contentResolver
                        .query(
                            contactUri,
                            arrayOf(ContactsContract.Contacts.DISPLAY_NAME),
                            null,
                            null,
                            null
                        )
                }

                cursor?.let {
                    if(it.count == 0)
                        return
                    else
                        it.moveToFirst()

                    vm.updateCrimeSuspect(it.getString(0))

                    updateUI()
                }
            }

            requestCode == REQUEST_CAMERA -> {
                updateUI(vm.getCrimePhoto())
            }
        }
    }

    //endregion

    //region UI setup
    private fun setupEditText() {
        binding.apply {
            txtCrimeTitle.addTextChangedListener(
                object : TextWatcher {
                    override fun beforeTextChanged(
                        s: CharSequence?,
                        start: Int,
                        count: Int,
                        after: Int
                    ) {}

                    override fun onTextChanged(
                        s: CharSequence?, start: Int, before: Int, count: Int) {
                        vm.updateCrimeTitle(s.toString())
                    }

                    override fun afterTextChanged(s: Editable?) {}
                }
            )

            txtCrimeDetails.addTextChangedListener(
                object : TextWatcher {
                    override fun beforeTextChanged(
                        s: CharSequence?,
                        start: Int,
                        count: Int,
                        after: Int
                    ) {}

                    override fun onTextChanged(
                        s: CharSequence?, start: Int, before: Int, count: Int) {
                        vm.updateCrimeDetails(s.toString())
                    }

                    override fun afterTextChanged(s: Editable?) {}
                }
            )
        }
    }

    private fun setupSolved() {
        binding.checkSolved.setOnCheckedChangeListener { _, isChecked ->
            vm.updateCrimeIsSolved(isChecked)
        }
    }

    private fun setupDateButton() {
        binding.btnDate.setOnClickListener {
            DatePickerFragment
                .getInstance(vm.cachedCrime.value?.date ?: Date())
                .apply {
                    setTargetFragment(this@CrimeDetailFragment, REQUEST_DATE)
                    show(
                        this@CrimeDetailFragment.parentFragmentManager,
                        DIALOG_DATE
                    )
                }
        }
    }

    private fun setupSuspectButton() {
        binding.btnSuspect.apply{
            val contactIntent = Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI)

            setOnClickListener {
                startActivityForResult(
                    contactIntent,
                    REQUEST_CONTACTS
                )
            }

            val contactActivities = requireActivity().packageManager
                .resolveActivity(contactIntent, PackageManager.MATCH_DEFAULT_ONLY)

            if(contactActivities == null) {
                isEnabled = false
            }
        }
    }

    private fun setupReportButton() {
        binding.btnReport.setOnClickListener {
            Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, createReport())
                putExtra(Intent.EXTRA_SUBJECT, getString(R.string.crime_report_subject))
            }.also { sendReportIntent ->
                startActivity(
                    Intent.createChooser(
                        sendReportIntent,
                        getString(R.string.crime_report_intent_title)
                    )
                )
            }
        }
    }

    private fun createReport(): String {
        val solvedString = getString(
            if(vm.cachedCrime.value?.isSolved == true) {
                R.string.crime_report_solved
            } else {
                R.string.crime_report_not_solved
            }
        )

        val dateString = vm.cachedCrime.value?.let { crime ->
            DateFormat.format(getString(R.string.crime_date_format), crime.date).toString()
        } ?: ""

        val suspect = getString(
            if(vm.cachedCrime.value?.suspect.isNullOrBlank()) {
                R.string.crime_report_no_suspect
            } else {
                R.string.crime_report_suspect
            }
        )

        return getString(
            R.string.crime_report_format,
            vm.cachedCrime.value?.title ?: "",
            dateString,
            solvedString,
            suspect
        )
    }

    private fun setupSaveButton() {
        binding.btnSave.setOnClickListener {
            vm.saveCrimeToDb()
        }
    }

    private fun setupCameraButton(photoFile: File) {
        val photoFileUri = FileProvider.getUriForFile(
            requireActivity(),
            "com.bignerdranch.android.criminalintent.fileprovider",
            photoFile
        )

        binding.btnCamera.apply {
            Toast.makeText(
                requireContext(),
                "testing camera",
                Toast.LENGTH_SHORT
            ).show()

            val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)

            val packageManager = requireActivity().packageManager
            val cameraResolveInfo = packageManager
                .resolveActivity(cameraIntent, PackageManager.MATCH_DEFAULT_ONLY)

            if(cameraResolveInfo == null) {
                isEnabled = false
            }

            setOnClickListener {
                cameraIntent.putExtra(
                    MediaStore.EXTRA_OUTPUT,
                    photoFileUri
                )

                val cameraActivities = packageManager.queryIntentActivities(
                    cameraIntent,
                    PackageManager.MATCH_DEFAULT_ONLY
                )

                for(cameraActivity in cameraActivities) {
                    requireActivity().grantUriPermission(
                        cameraActivity.activityInfo.packageName,
                        photoFileUri,
                        Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                    )
                }

                startActivityForResult(cameraIntent, REQUEST_CAMERA)
            }
        }
    }

    private fun setupImageButton() {
        binding.imgCrime.setOnClickListener {
            val photoFile = vm.getCrimePhoto()
            if(photoFile != null && photoFile.exists()) {
                ImageFragment.newInstance(
                    photoFile
                ).apply {
                    setTargetFragment(this@CrimeDetailFragment,
                        REQUEST_IMAGE
                    )
                    show(this@CrimeDetailFragment.requireFragmentManager(),
                        DIALOG_IMAGE
                    )
                }
            } else {
                Toast.makeText(
                    requireContext(),
                    "no photo available",
                    Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }

    private fun updateUI(photoFile: File? = null) {
        binding.apply {
            vm.cachedCrime.value?.let { crime ->
                txtCrimeTitle.setText(crime.title)
                txtCrimeDetails.setText(crime.details)
                checkSolved.isChecked = crime.isSolved
                btnDate.text =  DateFormat.format(
                    getString(R.string.crime_date_and_time_format),
                    crime.date
                ).toString()
                btnSuspect.text = if(crime.suspect.isNotEmpty()) {
                    crime.suspect
                } else {
                    getString(R.string.fragment_crime_suspect)
                }
            }
        }

        updatePhotoView(photoFile)
    }

    private fun updatePhotoView(photoFile: File?) {
        binding.imgCrime.apply {
            if(photoFile != null && photoFile.exists()) {
                setImageBitmap(getScaledBitmap(photoFile.path, requireActivity()))
            } else {
                setImageDrawable(null)
            }
        }
    }
    //endregion

    override fun onDateSelected(date: Date) {
        vm.updateCrimeDate(date)
        updateUI()
    }

    private fun setupObservers() {
        vm.exitFragmentEvent.observe(
            viewLifecycleOwner
        ) {
            requireActivity().onBackPressed()
        }

        vm.cachedCrime.observe(
            viewLifecycleOwner
        ) { crime ->
            crime.getPhotoFile(requireActivity()).let { photoFile ->
                updateUI(photoFile)
                setupCameraButton(photoFile)
            }
        }

        vm.canSaveCrime.observe(
            viewLifecycleOwner
        ) { canSave ->
            binding.btnSave.isEnabled = canSave
        }
    }

    companion object {
        fun getExistingCrimeInstance(crimeId: UUID): CrimeDetailFragment {
            val args = Bundle().apply {
                putSerializable(
                    ARG_CRIME_ID,
                    crimeId
                )
            }
            return CrimeDetailFragment()
                .apply {
                    arguments = args
                }
        }

        fun getNewCrimeInstance(): CrimeDetailFragment {
            return CrimeDetailFragment()
        }
    }
}
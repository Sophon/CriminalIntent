package com.bignerdranch.android.criminalintent

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.net.Uri
import android.os.Bundle
import android.provider.ContactsContract
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.text.format.DateFormat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.lifecycle.Observer
import java.io.File
import java.util.*

private const val ARG_CRIME_ID = "crime_id"
private const val DIALOG_DATE = "DialogDate"
private const val DIALOG_TIME = "DialogTime"
private const val DIALOG_IMAGE = "DialogImage"
private const val REQUEST_DATE = 0
private const val REQUEST_TIME = 1
private const val REQUEST_CONTACT = 2
private const val REQUEST_CAMERA = 3
private const val REQUEST_IMAGE = 4

private const val DATE_FORMAT = "EEE, MMM, dd"

class CrimeDetailFragment:
    Fragment(),
    DatePickerFragment.Callbacks,
    TimePickerFragment.Callbacks {

    private lateinit var crime: Crime
    private lateinit var photoFile: File
    private lateinit var photoFileUri: Uri

    private lateinit var photoView: ImageView
    private lateinit var photoButton: ImageButton
    private lateinit var titleField: EditText
    private lateinit var dateButton: Button
    private lateinit var isSolvedCheckbox: CheckBox
    private lateinit var sendReportButton: Button
    private lateinit var chooseSuspectButton: Button


    private val crimeDetailViewModel: CrimeDetailViewModel by lazy {
        ViewModelProviders
            .of(this)
            .get(CrimeDetailViewModel::class.java)
    }

    //==========

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        crime = Crime()

        val crimeId = arguments?.getSerializable(ARG_CRIME_ID) as UUID
        crimeDetailViewModel.loadCrime(crimeId)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view =
            inflater.inflate(R.layout.fragment_crime_detail, container, false)

        photoView = view.findViewById(R.id.crime_detail_photo)
        photoButton = view.findViewById(R.id.crime_detail_camera)
        titleField = view.findViewById(R.id.crime_title)
        dateButton = view.findViewById(R.id.crime_detail_date)
        isSolvedCheckbox = view.findViewById(R.id.crime_detail_solved)
        sendReportButton = view.findViewById(R.id.crime_detail_send_report)
        chooseSuspectButton = view.findViewById(R.id.crime_detail_choose_suspect)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        crimeDetailViewModel.crimeLiveData.observe(
            viewLifecycleOwner,
            Observer { crime ->
                crime?.let {
                    this.crime = crime

                    photoFile = crimeDetailViewModel.getPhotoFile(crime)
                    photoFileUri = FileProvider.getUriForFile(
                        requireActivity(),
                        "com.bignerdranch.android.criminalintent.fileprovider",
                        photoFile
                    )

                    updateUI()
                }
            }
        )
    }

    override fun onStart() {
        super.onStart()

        photoView.setOnClickListener {
            if(photoFile.exists()) {
                ImageFragment.newInstance(photoFile).apply {
                    setTargetFragment(this@CrimeDetailFragment, REQUEST_IMAGE)
                    show(this@CrimeDetailFragment.requireFragmentManager(), DIALOG_IMAGE)
                }
            } else {
                Toast.makeText(
                    requireContext(),
                    "no photo available",
                    Toast.LENGTH_SHORT)
                    .show()
            }
        }

        photoButton.apply {
            val takePhotoIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)

            val packageManager: PackageManager = requireActivity().packageManager
            val resolveInfo: ResolveInfo? = packageManager.resolveActivity(
                takePhotoIntent,
                PackageManager.MATCH_DEFAULT_ONLY
            )

            if(resolveInfo == null) {
                isEnabled = true
            }

            setOnClickListener {
                takePhotoIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoFileUri)

                val photoActivities = packageManager
                    .queryIntentActivities(
                        takePhotoIntent,
                        PackageManager.MATCH_DEFAULT_ONLY
                    )
                for(photoActivity in photoActivities) {
                    requireActivity().grantUriPermission(
                        photoActivity.activityInfo.packageName,
                        photoFileUri,
                        Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                    )
                }

                startActivityForResult(takePhotoIntent, REQUEST_CAMERA)
            }
        }

        val titleWatcher = object: TextWatcher {
            override fun beforeTextChanged(
                s: CharSequence?,
                start: Int,
                count: Int,
                after: Int) {}

            override fun onTextChanged(
                s: CharSequence?,
                start: Int,
                before: Int,
                count: Int) {
                crime.title = s.toString()
            }

            override fun afterTextChanged(s: Editable?) { }
        }
        titleField.addTextChangedListener(titleWatcher)

        isSolvedCheckbox.apply {
            setOnCheckedChangeListener { _, isChecked ->
                crime.isSolved = isChecked
            }
        }

        dateButton.setOnClickListener {
            DatePickerFragment.newInstance(crime.date).apply {
                setTargetFragment(this@CrimeDetailFragment, REQUEST_DATE)
                show(this@CrimeDetailFragment.requireFragmentManager(), DIALOG_DATE)
            }
        }

        chooseSuspectButton.apply {
            val chooseSuspectIntent = Intent(
                Intent.ACTION_PICK,
                ContactsContract.Contacts.CONTENT_URI
            )

            val packageManager: PackageManager = requireActivity().packageManager
            val resolveInfo: ResolveInfo? = packageManager.resolveActivity(
                chooseSuspectIntent,
                PackageManager.MATCH_DEFAULT_ONLY
            )

            if(resolveInfo == null) {
                isEnabled = false
            }

            setOnClickListener {
                startActivityForResult(chooseSuspectIntent, REQUEST_CONTACT)
            }
        }

        sendReportButton.setOnClickListener {
            Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"

                putExtra(Intent.EXTRA_TEXT, getCrimeReport())
                putExtra(Intent.EXTRA_SUBJECT, getString(R.string.crime_report_subject))
            }.also { intent ->
                val sendReportChooserIntent =
                    Intent.createChooser(intent, getString(R.string.send_report_prompt))

                startActivity(sendReportChooserIntent)
            }
        }
    }

    override fun onStop() {
        super.onStop()

        crimeDetailViewModel.saveCrime(crime)
    }

    override fun onDetach() {
        super.onDetach()

        revokeWritingPermissions()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when{
            resultCode != Activity.RESULT_OK -> return

            requestCode == REQUEST_CONTACT && data != null -> {
                val contactUri: Uri? = data.data

                //specify which fields we want our query to return
                val queryFields = arrayOf(ContactsContract.Contacts.DISPLAY_NAME)

                //perform query
                val cursor = requireActivity().contentResolver
                    .query(contactUri, queryFields, null, null, null)

                cursor?.use {
                    //verify cursor has >= 1 result
                    if(it.count == 0) return

                    //pull out first col first row (suspect)
                    it.moveToFirst()
                    val suspect = it.getString(0)
                    crime.suspect = suspect
                }

                crimeDetailViewModel.saveCrime(crime)
                chooseSuspectButton.text = crime.suspect
            }

            requestCode == REQUEST_CAMERA -> {
                revokeWritingPermissions()
                updatePhotoView()
            }
        }
    }

    override fun onDateSelected(date: Date) {
        crime.date = date

        TimePickerFragment.newInstance(Time.dateToTime(crime.date)).apply {
            setTargetFragment(this@CrimeDetailFragment, REQUEST_TIME)
            show(this@CrimeDetailFragment.requireFragmentManager(), DIALOG_TIME)
        }
    }

    override fun onTimeSelected(time: Time) {
        crime.date = Time.addTimeToDate(crime.date, time)

        updateUI()
    }

    //==========

    private fun updateUI() {
        titleField.setText(crime.title)

        dateButton.text = crime.date.toString()

        isSolvedCheckbox.apply {
            isChecked = crime.isSolved
            jumpDrawablesToCurrentState()
        }

        if(crime.suspect.isNotEmpty()) {
            chooseSuspectButton.text = crime.suspect
        }

        updatePhotoView()
    }

    private fun getCrimeReport(): String {
        val solvedString = if(crime.isSolved) {
            getString(R.string.crime_report_solved)
        } else {
            getString(R.string.crime_report_unsolved)
        }

        val dateString = DateFormat.format(DATE_FORMAT, crime.date).toString()
        val suspectString = if(crime.suspect.isBlank()) {
            getString(R.string.crime_report_no_suspect)
        } else {
            getString(R.string.crime_report_suspect, crime.suspect)
        }

        return getString(
            R.string.crime_report,
            crime.title, dateString, solvedString, suspectString
        )
    }

    private fun revokeWritingPermissions() {
        requireActivity().revokeUriPermission(
            photoFileUri,
            Intent.FLAG_GRANT_WRITE_URI_PERMISSION
        )
    }

    private fun updatePhotoView() {
        if(photoFile.exists()) {
            val bitmap = getScaledBitmap(photoFile.path, requireActivity())
            photoView.setImageBitmap(bitmap)
        } else {
            photoView.setImageDrawable(null)
        }
    }

    //==========

    companion object {

        fun newInstance(crimeId: UUID): CrimeDetailFragment {
            val argBundle = Bundle().apply {
                putSerializable(ARG_CRIME_ID, crimeId)
            }

            return CrimeDetailFragment().apply {
                arguments = argBundle
            }
        }
    }
}
package com.bignerdranch.android.criminalintent.ui

import android.app.Dialog
import android.os.Bundle
import android.widget.ImageView
import androidx.fragment.app.DialogFragment
import com.bignerdranch.android.criminalintent.R
import com.bignerdranch.android.criminalintent.utils.getScaledBitmap
import java.io.File

private const val ARG_FILE = "file"

class ImageFragment: DialogFragment() {

    private lateinit var photoView: ImageView

    //==========

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val photoFile = arguments?.getSerializable(ARG_FILE) as File

        val dialog = Dialog(requireContext())
        dialog.setContentView(R.layout.dialog_crime_photo)

        photoView = dialog.findViewById(R.id.crime_photo_zoomed)

        if(photoFile.exists()) {
            val bitmap =
                getScaledBitmap(
                    photoFile.path,
                    requireActivity()
                )
            photoView.setImageBitmap(bitmap)
        } else {
            photoView.setImageBitmap(null)
        }

        return dialog
    }

    //==========

    companion object {

        fun newInstance(photoFile: File): ImageFragment {
            val argBundle = Bundle().apply {
                putSerializable(ARG_FILE, photoFile)
            }

            return ImageFragment().apply {
                arguments = argBundle
            }
        }
    }
}
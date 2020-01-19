package com.bignerdranch.android.criminalintent

import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import androidx.fragment.app.DialogFragment

class EmptyAlertFragment: DialogFragment() {

    interface Callbacks {
        fun onCreateSelected()
    }

    //==========

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(activity!!)

        builder.setPositiveButton("Create") {
                _, _ ->
            targetFragment?.let { fragment ->
                (fragment as Callbacks).onCreateSelected()
            }
        }
        builder.setNegativeButton("Cancel") {
                dialog, _ ->
            dialog.dismiss()
        }

        val alert = builder.create()

        alert.apply {
            setTitle("Crime list empty!")
            setMessage("Do you want to create a new crime?")
        }

        return alert
    }

    //==========

    companion object {
        fun newInstance(): EmptyAlertFragment {
            return EmptyAlertFragment()
        }
    }
}
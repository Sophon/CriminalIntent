package com.bignerdranch.android.criminalintent.domain.model

import android.content.Context
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.File
import java.util.*

@Entity
data class Crime(
    @PrimaryKey
    val id: UUID = UUID.randomUUID(),
    var title: String = "",
    var details: String = "",
    var date: Date = Date(),
    var isSolved: Boolean = false,
    var suspect: String = ""
) {

    constructor(crime: Crime):
            this(
                crime.id,
                crime.title,
                crime.details,
                crime.date,
                crime.isSolved,
                crime.suspect
            )

    val photoFileName
        get() = "IMG_$id.jpg"

    fun getPhotoFile(context: Context): File {
        return File(context.applicationContext.filesDir, photoFileName)
    }
}

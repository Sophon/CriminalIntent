package com.bignerdranch.android.criminalintent.Model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity
class Crime(
    @PrimaryKey val id: UUID = UUID.randomUUID(),
    var title: String = "",
    var date: Date = Date(),
    var isSolved: Boolean = false,
    var suspect: String = ""
) {

    val photoFileName
        get() = "IMG_$id.jpg"

    //==========

    override fun equals(other: Any?): Boolean {
        if(other == null || other !is Crime) {
            return false
        }

        return (title == other.title)
                && (date == other.date)
                && (isSolved == other.isSolved)
    }
}
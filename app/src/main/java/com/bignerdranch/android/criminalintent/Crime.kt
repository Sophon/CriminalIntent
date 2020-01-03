package com.bignerdranch.android.criminalintent

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity
class Crime(
    @PrimaryKey val id: UUID = UUID.randomUUID(),
    var title: String = "",
    var date: Date = Date(),
    var isSolved: Boolean = false
) {

    override fun equals(other: Any?): Boolean {
        if(other == null || other !is Crime) {
            return false
        }

        return (title == other.title)
                && (date == other.date)
                && (isSolved == other.isSolved)
    }
}
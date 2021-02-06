package com.bignerdranch.android.criminalintent.domain.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.bignerdranch.android.criminalintent.domain.db.CrimeTypeConverters
import com.bignerdranch.android.criminalintent.domain.model.Crime

@Database(
    entities = [ Crime::class ],
    version = 1
)
@TypeConverters(CrimeTypeConverters::class)
abstract class CrimeDB: RoomDatabase() {

    abstract fun dao(): CrimeDao
}
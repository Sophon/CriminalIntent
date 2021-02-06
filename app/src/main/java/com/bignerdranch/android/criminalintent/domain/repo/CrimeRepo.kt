package com.bignerdranch.android.criminalintent.domain.repo

import android.content.Context
import androidx.room.Room
import com.bignerdranch.android.criminalintent.domain.db.CrimeDB
import com.bignerdranch.android.criminalintent.domain.model.Crime
import kotlinx.coroutines.flow.Flow
import timber.log.Timber
import java.io.File
import java.lang.IllegalStateException
import java.util.*

private const val DB_NAME = "crime database"

class CrimeRepository private constructor(context: Context) {

    private val filesDir = context.applicationContext.filesDir

    private val db: CrimeDB = Room.databaseBuilder(
        context,
        CrimeDB::class.java,
        DB_NAME
    )
        .build()

    private val dao = db.dao()

    fun getCrimesFlow(): Flow<List<Crime>> = dao.getCrimes()

    fun getCrimeFlow(uuid: UUID): Flow<Crime?> = dao.getCrime(uuid)

    suspend fun addCrime(crime: Crime) = dao.addCrime(crime)

    suspend fun deleteCrime(crime: Crime) = dao.deleteCrime(crime)

    fun getPhotoFile(crime: Crime?): File? {
        return crime?.let {
            File(filesDir, crime.photoFileName)
        }
    }

    companion object {
        private var INSTANCE: CrimeRepository? = null

        fun initialize(context: Context) {
            if(INSTANCE == null) {
                INSTANCE = CrimeRepository(context)

                Timber.d("Database: initialized")
            }
        }

        fun get(): CrimeRepository {
            return INSTANCE
                ?: throw IllegalStateException("CrimeRepository must be initialized")
        }
    }
}
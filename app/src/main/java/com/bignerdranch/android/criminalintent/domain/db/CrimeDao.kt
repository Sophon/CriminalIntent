package com.bignerdranch.android.criminalintent.domain.db

import androidx.room.*
import com.bignerdranch.android.criminalintent.domain.model.Crime
import kotlinx.coroutines.flow.Flow
import java.util.*

@Dao
interface CrimeDao {

    @Query("SELECT * FROM crime ORDER BY date")
    fun getCrimes(): Flow<List<Crime>>

    @Query("SELECT * FROM crime WHERE id=(:uuid)")
    fun getCrime(uuid: UUID): Flow<Crime?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addCrime(crime: Crime)

    @Delete
    suspend fun deleteCrime(crime: Crime)
}
package com.example.weight_tracker

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface WeightDao {
    // Only get weights where the email matches the current user
    @Query("SELECT * FROM weight_table WHERE userEmail = :email ORDER BY date DESC")
    fun getWeightsForUser(email: String): LiveData<List<WeightEntry>>

    @Insert
    suspend fun insert(entry: WeightEntry)

    @Delete
    suspend fun delete(entry: WeightEntry)
}
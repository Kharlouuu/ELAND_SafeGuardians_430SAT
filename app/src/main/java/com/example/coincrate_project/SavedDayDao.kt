package com.example.coincrate_project

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface SavedDayDao {

    @Query("DELETE FROM saved_day")
    suspend fun clearAll()

    @Query("SELECT * FROM saved_day")
    suspend fun getAll(): List<SavedDay>

    @Query("DELETE FROM saved_day")
    suspend fun deleteAll()

    @Query("SELECT * FROM saved_day WHERE date = :date LIMIT 1")
    suspend fun getByDate(date: String): SavedDay?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(savedDay: SavedDay)

    @Query("SELECT COUNT(*) FROM saved_day WHERE date BETWEEN :startDate AND :endDate")
    suspend fun getCountInRange(startDate: String, endDate: String): Int
}
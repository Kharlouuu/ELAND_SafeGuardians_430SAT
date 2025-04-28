package com.example.coincrate_project

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface TransactionDao {

    @Insert
    suspend fun insert(transaction: TransactionEntity)

    @Update
    suspend fun update(transaction: TransactionEntity)

    @Delete
    suspend fun delete(transaction: TransactionEntity)

    // This returns LiveData - for observing
    @Query("SELECT * FROM transactions ORDER BY timestamp DESC")
    fun getAllTransactionsLive(): LiveData<List<TransactionEntity>>

    // New: This returns List directly - for suspend function
    @Query("SELECT * FROM transactions ORDER BY timestamp DESC")
    suspend fun getAllTransactions(): List<TransactionEntity>

    @Query("SELECT * FROM transactions WHERE type = :type ORDER BY timestamp DESC")
    fun getTransactionsByType(type: String): LiveData<List<TransactionEntity>>

    @Query("SELECT SUM(amount) FROM transactions WHERE type = 'Savings'")
    fun getTotalSavings(): LiveData<Double?>

    @Query("SELECT SUM(amount) FROM transactions WHERE type = 'Expenses'")
    fun getTotalExpenses(): LiveData<Double?>

    @Query("SELECT * FROM transactions WHERE id = :id")
    suspend fun getTransactionById(id: Int): TransactionEntity?
}
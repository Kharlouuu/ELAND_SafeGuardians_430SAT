package com.example.coincrate_project

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "transactions")
data class TransactionEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val amount: Double,
    val type: String, // "Expenses" or "Savings"
    val category: String = "",       // new field with default
    val note: String = "",
    val date: String = "",
    val timestamp: Long = System.currentTimeMillis()
) {
    fun getFormattedAmount(): String = "₱%.2f".format(amount)
}
package com.example.coincrate_project

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "saved_day")
data class SavedDay(
    @PrimaryKey val date: String,
    val amountSaved: Double
)
package com.example.coincrate_project

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class GoalEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val amount: Double,
    val isAchieved: Boolean = false
)
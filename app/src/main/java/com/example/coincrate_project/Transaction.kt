package com.example.coincrate_project

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Transaction(
    val name: String,
    val amount: Double,
    val type: String // "Savings" or "Expenses"
) : Parcelable

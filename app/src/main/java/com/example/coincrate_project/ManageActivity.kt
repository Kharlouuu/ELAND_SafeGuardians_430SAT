package com.example.coincrate_project

import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity


class ManageActivity : AppCompatActivity() {

    private lateinit var tvTotalSavings: TextView
    private lateinit var btnEditBudget: ImageView
    private lateinit var btnSavings: LinearLayout
    private lateinit var btnExpenses: LinearLayout

    private lateinit var db: AppDatabase
    private var totalSavings: Double = 0.0

    private val transactionsList = ArrayList<MainActivity.Transaction>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.manage_savings_page)

        tvTotalSavings = findViewById(R.id.tvTotalSavings)
        btnEditBudget = findViewById(R.id.btnEditBudget)
        btnSavings = findViewById(R.id.btnSavings)
        btnExpenses = findViewById(R.id.btnExpenses)

        db = AppDatabase.getDatabase(this)

        loadTotalSavings()

        btnEditBudget.setOnClickListener {
            showEditDialog()
        }

        btnSavings.setOnClickListener {
            startActivity(Intent(this, SavingsActivity::class.java))

        }

        btnExpenses.setOnClickListener {
            startActivity(Intent(this, ExpensesActivity::class.java))
        }
    }

    private fun loadTotalSavings() {
        val prefs = getSharedPreferences("budget_prefs", MODE_PRIVATE)
        totalSavings = prefs.getFloat("total_savings", 0.0f).toDouble()
        updateSavingsDisplay()
    }

    private fun updateSavingsDisplay() {
        tvTotalSavings.text = "₱%.2f".format(totalSavings)
    }

    private fun addToTotalSavings(additionalAmount: Double) {
        totalSavings += additionalAmount
        val prefs = getSharedPreferences("budget_prefs", MODE_PRIVATE)
        prefs.edit().putFloat("total_savings", totalSavings.toFloat()).apply()
        updateSavingsDisplay()
        Toast.makeText(this, "Savings added!", Toast.LENGTH_SHORT).show()
    }

    private fun showEditDialog() {
        val input = EditText(this).apply {
            inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL
            hint = "Enter additional savings amount"
        }

        AlertDialog.Builder(this)
            .setTitle("Add Monthly Savings")
            .setView(input)
            .setPositiveButton("Add") { _, _ ->
                val amountToAdd = input.text.toString().toDoubleOrNull()
                if (amountToAdd != null && amountToAdd >= 0) {
                    addToTotalSavings(amountToAdd)
                } else {
                    Toast.makeText(this, "Invalid amount", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
}

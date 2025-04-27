package com.example.coincrate_project

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class AddExpenseActivity : AppCompatActivity() {

    private lateinit var expenseName: EditText
    private lateinit var amount: EditText
    private lateinit var saveButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.expense_page)

        expenseName = findViewById(R.id.editExpense)
        amount = findViewById(R.id.editAmount)
        saveButton = findViewById(R.id.buttonSave)

        saveButton.setOnClickListener {
            val name = expenseName.text.toString().trim()
            val amt = amount.text.toString().trim()

            if (name.isNotEmpty() && amt.isNotEmpty()) {
                val expenseAmount = amt.toFloatOrNull()
                if (expenseAmount == null || expenseAmount <= 0f) {
                    Toast.makeText(this, "Enter a valid amount", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
                val prefsNotify = getSharedPreferences("notifications", MODE_PRIVATE)
                prefsNotify.edit().putString("latest_message", "You have successfully added an expense for this month").apply()

                val prefs = getSharedPreferences("budget_prefs", MODE_PRIVATE)
                val currentSavings = prefs.getFloat("total_savings", 0.0f)

                if (currentSavings >= expenseAmount) {
                    // Deduct the amount from savings
                    val newSavings = currentSavings - expenseAmount
                    prefs.edit().putFloat("total_savings", newSavings).apply()

                    // Send data back to previous activity
                    val resultIntent = Intent()
                    resultIntent.putExtra("expense_name", name)
                    resultIntent.putExtra("expense_amount", amt)
                    resultIntent.putExtra("expense_type", "Expenses")
                    setResult(Activity.RESULT_OK, resultIntent)
                    finish()
                } else {
                    Toast.makeText(this, "Low amount. Not enough savings.", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Please enter name and amount", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
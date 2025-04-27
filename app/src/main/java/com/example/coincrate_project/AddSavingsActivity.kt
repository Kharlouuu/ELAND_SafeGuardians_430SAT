package com.example.coincrate_project

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class AddSavingsActivity : AppCompatActivity() {

    private lateinit var savingsName: EditText
    private lateinit var amount: EditText
    private lateinit var saveButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.savings_page)

        savingsName = findViewById(R.id.editSavings)
        amount = findViewById(R.id.editSavingAmount)
        saveButton = findViewById(R.id.buttonSaveSaving)

        saveButton.setOnClickListener {
            val name = savingsName.text.toString().trim()
            val amt = amount.text.toString().trim()

            if (name.isNotEmpty() && amt.isNotEmpty()) {
                val resultIntent = Intent()
                resultIntent.putExtra("expense_name", name)
                resultIntent.putExtra("expense_amount", amt)
                resultIntent.putExtra("expense_type", "Savings")
                setResult(Activity.RESULT_OK, resultIntent)
                finish()
            } else {
                Toast.makeText(this, "Please enter name and amount", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
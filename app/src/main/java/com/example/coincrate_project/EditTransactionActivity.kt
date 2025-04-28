package com.example.coincrate_project

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class EditTransactionActivity : AppCompatActivity() {

    private lateinit var etName: EditText
    private lateinit var etAmount: EditText
    private lateinit var tvType: TextView
    private lateinit var btnSave: Button

    private var transactionId: Int = -1
    private lateinit var db: AppDatabase
    private lateinit var transactionType: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.edit_transaction)

        etName = findViewById(R.id.etEditName)
        etAmount = findViewById(R.id.etEditAmount)
        tvType = findViewById(R.id.tvEditType)
        btnSave = findViewById(R.id.btnEditSave)

        db = AppDatabase.getDatabase(this)

        transactionId = intent.getIntExtra("transaction_id", -1)

        if (transactionId != -1) {
            loadTransaction(transactionId)
        }

        btnSave.setOnClickListener {
            updateTransaction()
        }
    }

    private fun loadTransaction(id: Int) {
        CoroutineScope(Dispatchers.IO).launch {
            val transaction = db.transactionDao().getTransactionById(id)
            transaction?.let {
                transactionType = it.type
                withContext(Dispatchers.Main) {
                    etName.setText(it.name)
                    etAmount.setText(it.amount.toString()) // FIXED
                    tvType.text = it.type
                }
            }
        }
    }

    private fun updateTransaction() {
        val name = etName.text.toString().trim()
        val amountText = etAmount.text.toString().trim()

        if (name.isEmpty() || amountText.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
            return
        }

        val amount = amountText.toDoubleOrNull()
        if (amount == null) {
            Toast.makeText(this, "Invalid amount format", Toast.LENGTH_SHORT).show()
            return
        }

        val updatedTransaction = TransactionEntity(
            id = transactionId,
            name = name,
            amount = amount,
            type = transactionType,
            timestamp = System.currentTimeMillis()
        )

        CoroutineScope(Dispatchers.IO).launch {
            db.transactionDao().update(updatedTransaction)
            withContext(Dispatchers.Main) {
                Toast.makeText(
                    this@EditTransactionActivity,
                    "Transaction updated",
                    Toast.LENGTH_SHORT
                ).show()
                finish()
            }
        }
    }
}
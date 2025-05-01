package com.example.coincrate_project

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class SavingsActivity : AppCompatActivity() {

    private lateinit var rvSavings: RecyclerView
    private lateinit var transactionManageAdapter: TransactionManageAdapter
    private val savingsList = mutableListOf<MainActivity.Transaction>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.savings_category_page)

        rvSavings = findViewById(R.id.recyclerSavings)

        loadSavings()

        transactionManageAdapter = TransactionManageAdapter(
            savingsList,
            onEdit = { position -> showEditDialog(position) },
            onDelete = { position -> deleteSaving(position) }
        )

        rvSavings.layoutManager = LinearLayoutManager(this)
        rvSavings.adapter = transactionManageAdapter
    }

    private fun loadSavings() {
        val prefs = getSharedPreferences("transactions_prefs", MODE_PRIVATE)
        val transactionsString = prefs.getString("transactions_list", "") ?: ""
        val transactions = transactionsString.split("#")
            .mapNotNull { item ->
                val parts = item.split("|")
                if (parts.size == 3) {
                    val name = parts[0]
                    val type = parts[1]
                    val amount = parts[2].toDoubleOrNull() ?: 0.0
                    MainActivity.Transaction(name, type, amount)
                } else null
            }
            .filter { it.type == "Savings" } // Only savings transactions

        savingsList.clear()
        savingsList.addAll(transactions)
    }

    private fun saveSavings() {
        val prefs = getSharedPreferences("transactions_prefs", MODE_PRIVATE)
        val editor = prefs.edit()
        val allTransactionsString = savingsList.joinToString("#") { "${it.name}|${it.type}|${it.amount}" }
        editor.putString("transactions_list", allTransactionsString)
        editor.apply()
    }

    private fun showEditDialog(position: Int) {
        val saving = savingsList[position]

        val dialogView = LayoutInflater.from(this).inflate(R.layout.edit_transaction, null)
        val etEditName = dialogView.findViewById<EditText>(R.id.etEditName)
        val etEditAmount = dialogView.findViewById<EditText>(R.id.etEditAmount)
        val tvEditType = dialogView.findViewById<TextView>(R.id.tvEditType)
        val btnEditSave = dialogView.findViewById<Button>(R.id.btnEditSave)
        val btnCancel = dialogView.findViewById<Button>(R.id.btnCancel)

        etEditName.setText(saving.name)
        etEditAmount.setText(saving.amount.toString())
        tvEditType.text = saving.type // Always "Savings"

        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .create()

        btnEditSave.setOnClickListener {
            val newName = etEditName.text.toString().trim()
            val newAmount = etEditAmount.text.toString().toDoubleOrNull()

            if (newName.isEmpty() || newAmount == null || newAmount < 0) {
                Toast.makeText(this, "Please enter valid details", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            savingsList[position] = MainActivity.Transaction(newName, "Savings", newAmount)
            transactionManageAdapter.notifyItemChanged(position)
            saveSavings()
            dialog.dismiss()
            Toast.makeText(this, "Saving updated!", Toast.LENGTH_SHORT).show()
        }

        btnCancel.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun deleteSaving(position: Int) {
        savingsList.removeAt(position)
        transactionManageAdapter.notifyItemRemoved(position)
        saveSavings()
        Toast.makeText(this, "Saving deleted!", Toast.LENGTH_SHORT).show()
    }
}
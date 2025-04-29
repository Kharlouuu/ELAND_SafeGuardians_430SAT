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

class ExpensesActivity : AppCompatActivity() {

    private lateinit var rvExpenses: RecyclerView
    private lateinit var transactionManageAdapter: TransactionManageAdapter
    private val expensesList = mutableListOf<MainActivity.Transaction>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.expense_category_page)

        rvExpenses = findViewById(R.id.recyclerExpenses)

        loadExpenses()

        transactionManageAdapter = TransactionManageAdapter(
            expensesList,
            onEdit = { position -> showEditDialog(position) },
            onDelete = { position -> confirmDelete(position) }
        )

        rvExpenses.layoutManager = LinearLayoutManager(this)
        rvExpenses.adapter = transactionManageAdapter
    }

    private fun loadExpenses() {
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
            .filter { it.type == "Expenses" } // Only expense transactions

        expensesList.clear()
        expensesList.addAll(transactions)
    }

    private fun saveExpenses() {
        val prefs = getSharedPreferences("transactions_prefs", MODE_PRIVATE)
        val editor = prefs.edit()
        val allTransactionsString = expensesList.joinToString("#") { "${it.name}|${it.type}|${it.amount}" }
        editor.putString("transactions_list", allTransactionsString)
        editor.apply()
    }

    private fun showEditDialog(position: Int) {
        val expense = expensesList[position]

        val dialogView = LayoutInflater.from(this).inflate(R.layout.edit_transaction, null)
        val etEditName = dialogView.findViewById<EditText>(R.id.etEditName)
        val etEditAmount = dialogView.findViewById<EditText>(R.id.etEditAmount)
        val tvEditType = dialogView.findViewById<TextView>(R.id.tvEditType)
        val btnEditSave = dialogView.findViewById<Button>(R.id.btnEditSave)

        etEditName.setText(expense.name)
        etEditAmount.setText(expense.amount.toString())
        tvEditType.text = expense.type // Always "Expenses"

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

            expensesList[position] = MainActivity.Transaction(newName, "Expenses", newAmount)
            transactionManageAdapter.notifyItemChanged(position)
            saveExpenses()
            dialog.dismiss()
            Toast.makeText(this, "Expense updated!", Toast.LENGTH_SHORT).show()
        }

        dialog.show()
    }

    private fun confirmDelete(position: Int) {
        AlertDialog.Builder(this)
            .setTitle("Delete Expense")
            .setMessage("Are you sure you want to delete this expense?")
            .setPositiveButton("Yes") { _, _ ->
                expensesList.removeAt(position)
                transactionManageAdapter.notifyItemRemoved(position)
                saveExpenses()
                Toast.makeText(this, "Expense deleted!", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("No", null)
            .show()
    }
}

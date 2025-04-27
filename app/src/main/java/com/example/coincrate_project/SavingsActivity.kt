package com.example.coincrate_project

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class SavingsActivity : AppCompatActivity() {

    private lateinit var adapter: TransactionManageAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var db: AppDatabase
    private val transactionList = mutableListOf<TransactionEntity>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.savings_category_page)

        db = AppDatabase.getDatabase(this)
        recyclerView = findViewById(R.id.recyclerSavings)
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Use TransactionManageAdapter.kt
        adapter = TransactionManageAdapter(
            transactionList,
            onEditClick = { transaction -> showEditDialog(transaction) },
            onDeleteClick = { transaction -> deleteTransaction(transaction) }
        )

        recyclerView.adapter = adapter

        observeSavingsTransactions()

        findViewById<ImageButton>(R.id.btnBack).setOnClickListener {
            finish()
        }
    }

    private fun observeSavingsTransactions() {
        db.transactionDao().getTransactionsByType("Savings").observe(this) { list ->
            transactionList.clear()
            transactionList.addAll(list)
            adapter.notifyDataSetChanged()
        }
    }

    private fun showEditDialog(transaction: TransactionEntity) {
        val intent = Intent(this, EditTransactionActivity::class.java)
        intent.putExtra("transaction_id", transaction.id)
        startActivity(intent)
    }

    private fun deleteTransaction(transaction: TransactionEntity) {
        AlertDialog.Builder(this)
            .setTitle("Delete Transaction")
            .setMessage("Are you sure you want to delete this?")
            .setPositiveButton("Yes") { _, _ ->
                CoroutineScope(Dispatchers.IO).launch {
                    db.transactionDao().delete(transaction)
                    withContext(Dispatchers.Main) {
                        Toast.makeText(this@SavingsActivity, "Deleted", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            .setNegativeButton("No", null)
            .show()
    }
}
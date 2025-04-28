package com.example.coincrate_project

import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class TransactionActivity : AppCompatActivity() {

    private lateinit var rvTransactions: RecyclerView
    private lateinit var transactionsAdapter: TransactionAdapter

    private val transactionsList = ArrayList<MainActivity.Transaction>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.transaction_page)

        val btnBack = findViewById<ImageView>(R.id.btnBack)
        btnBack.setOnClickListener {
            finish()
        }

        rvTransactions = findViewById(R.id.rvAllTransactions)

        transactionsList.addAll(loadTransactions())

        transactionsAdapter = TransactionAdapter(transactionsList)
        rvTransactions.layoutManager = LinearLayoutManager(this)
        rvTransactions.adapter = transactionsAdapter

    }


    private fun loadTransactions(): List<MainActivity.Transaction> {
        val prefs = getSharedPreferences("transactions_prefs", MODE_PRIVATE)
        val transactionsString = prefs.getString("transactions_list", "") ?: ""
        if (transactionsString.isEmpty()) return emptyList()

        return transactionsString.split("#").mapNotNull { entry ->
            val parts = entry.split("|")
            if (parts.size == 3) {
                val name = parts[0]
                val type = parts[1]
                val amount = parts[2].toDoubleOrNull() ?: return@mapNotNull null
                MainActivity.Transaction(name, type, amount)
            } else {
                null
            }
        }
    }

    class TransactionAdapter(private val transactions: List<MainActivity.Transaction>) :
        RecyclerView.Adapter<TransactionAdapter.ViewHolder>() {

        inner class ViewHolder(view: android.view.View) : RecyclerView.ViewHolder(view) {
            val tvName: TextView = view.findViewById(R.id.tvExpenseTitle)
            val tvAmount: TextView = view.findViewById(R.id.tvExpenseAmount)
            val tvType: TextView = view.findViewById(R.id.tvExpenseType)
        }

        override fun onCreateViewHolder(parent: android.view.ViewGroup, viewType: Int): ViewHolder {
            val view = android.view.LayoutInflater.from(parent.context)
                .inflate(R.layout.item_transaction, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val transaction = transactions[position]
            holder.tvName.text = transaction.name

            val formattedAmount = if (transaction.type == "Expenses") {
                "₱-%,.2f".format(transaction.amount)
            } else {
                "₱%,.2f".format(transaction.amount)
            }
            holder.tvAmount.text = formattedAmount
            holder.tvType.text = transaction.type

            holder.tvType.setTextColor(
                when (transaction.type) {
                    "Expenses" -> android.graphics.Color.parseColor("#F44336") // Red
                    "Savings" -> android.graphics.Color.parseColor("#4CAF50") // Green
                    else -> android.graphics.Color.BLACK
                }
            )
        }

        override fun getItemCount(): Int = transactions.size
    }
}
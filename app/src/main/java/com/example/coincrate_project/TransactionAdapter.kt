package com.example.coincrate_project

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class TransactionAdapter(private val transactions: List<Transaction>) :
    RecyclerView.Adapter<TransactionAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvName: TextView = view.findViewById(R.id.tvExpenseTitle)
        val tvAmount: TextView = view.findViewById(R.id.tvExpenseAmount)
        val tvType: TextView = view.findViewById(R.id.tvExpenseType)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_transaction, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val transaction = transactions[position]
        holder.tvName.text = transaction.name

        val amountDouble = transaction.amount.toString().toDoubleOrNull() ?: 0.0

        val formattedAmount = if (transaction.type == "Expenses") {
            String.format("₱-%,.2f", amountDouble)
        } else {
            String.format("₱%,.2f", amountDouble)
        }
        holder.tvAmount.text = formattedAmount
        holder.tvType.text = transaction.type

        holder.tvType.setTextColor(
            when (transaction.type) {
                "Expenses" -> Color.parseColor("#F44336")
                "Savings" -> Color.parseColor("#4CAF50")
                else -> Color.BLACK
            }
        )
    }


    override fun getItemCount(): Int = transactions.size
}
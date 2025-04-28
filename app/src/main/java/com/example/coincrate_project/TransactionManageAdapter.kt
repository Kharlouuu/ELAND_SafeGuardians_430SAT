package com.example.coincrate_project

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class TransactionManageAdapter(private val transactions: List<Transaction>) :
    RecyclerView.Adapter<TransactionManageAdapter.ViewHolder>() {

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

        val formattedAmount = if (transaction.type == "Expenses") {
            "₱-%,.2f".format(transaction.amount)
        } else {
            "₱%,.2f".format(transaction.amount)
        }
        holder.tvAmount.text = formattedAmount
        holder.tvType.text = transaction.type

        holder.tvType.setTextColor(
            when (transaction.type) {
                "Expenses" -> Color.parseColor("#F44336") // Red
                "Savings" -> Color.parseColor("#4CAF50") // Green
                else -> Color.BLACK
            }
        )
    }

    override fun getItemCount(): Int = transactions.size
}
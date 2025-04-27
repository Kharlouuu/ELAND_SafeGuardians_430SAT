package com.example.coincrate_project

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import android.graphics.Color

class TransactionManageAdapter(
    private val transactions: List<TransactionEntity>,
    private val onEditClick: (TransactionEntity) -> Unit,
    private val onDeleteClick: (TransactionEntity) -> Unit
) : RecyclerView.Adapter<TransactionManageAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvName: TextView = view.findViewById(R.id.tvExpenseTitle)
        val tvAmount: TextView = view.findViewById(R.id.tvExpenseAmount)
        val tvType: TextView = view.findViewById(R.id.tvExpenseType)
        val btnEdit: ImageButton = view.findViewById(R.id.btnEdit)
        val btnDelete: ImageButton = view.findViewById(R.id.btnDelete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_transaction_manage, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val transaction = transactions[position]
        holder.tvName.text = transaction.name
        holder.tvAmount.text = "₱%,.2f".format(transaction.amount)
        holder.tvType.text = transaction.type

        holder.tvType.setTextColor(
            when (transaction.type) {
                "Expenses" -> Color.parseColor("#F44336")
                "Savings" -> Color.parseColor("#4CAF50")
                else -> Color.BLACK
            }
        )

        holder.btnEdit.setOnClickListener { onEditClick(transaction) }
        holder.btnDelete.setOnClickListener { onDeleteClick(transaction) }
    }

    override fun getItemCount(): Int = transactions.size
}
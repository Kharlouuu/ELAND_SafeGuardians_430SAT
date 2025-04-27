package com.example.coincrate_project

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class TransactionAdapter(
    private val transactions: List<TransactionEntity>,
    private val onEditClick: (TransactionEntity) -> Unit,
    private val onDeleteClick: (TransactionEntity) -> Unit
) : RecyclerView.Adapter<TransactionAdapter.TransactionViewHolder>() {

    inner class TransactionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvTitle: TextView = itemView.findViewById(R.id.tvExpenseTitle)
        private val tvAmount: TextView = itemView.findViewById(R.id.tvExpenseAmount)
        private val tvType: TextView = itemView.findViewById(R.id.tvExpenseType)

        fun bind(transaction: TransactionEntity) {
            // Set the transaction name and type
            tvTitle.text = transaction.name
            tvType.text = transaction.type

            // Format the amount with comma separators and minus sign for expenses
            val formattedAmount = if (transaction.type == "Expenses") {
                "₱-${"%,.2f".format(transaction.amount)}"
            } else {
                "₱${"%,.2f".format(transaction.amount)}"
            }
            tvAmount.text = formattedAmount

            val color = when (transaction.type) {
                "Expenses" -> Color.parseColor("#F44336") // Red
                "Savings" -> Color.parseColor("#4CAF50")  // Green
                else -> Color.BLACK
            }
            tvType.setTextColor(color)

            itemView.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onEditClick(transactions[position])
                }
            }

            itemView.setOnLongClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onDeleteClick(transactions[position])
                }
                true
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_transaction, parent, false)
        return TransactionViewHolder(view)
    }

    override fun onBindViewHolder(holder: TransactionViewHolder, position: Int) {
        holder.bind(transactions[position])
    }

    override fun getItemCount(): Int = transactions.size
}
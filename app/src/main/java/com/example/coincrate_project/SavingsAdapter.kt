package com.example.coincrate_project

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import androidx.appcompat.app.AlertDialog

class SavingsAdapter(
    private val savingsList: List<MainActivity.Transaction>,
    private val onEdit: (Int) -> Unit,
    private val onDelete: (Int) -> Unit
) : RecyclerView.Adapter<SavingsAdapter.ViewHolder>() {

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
        val transaction = savingsList[position]
        holder.tvName.text = transaction.name
        holder.tvAmount.text = "\u20B1%,.2f".format(transaction.amount)
        holder.tvType.text = transaction.type

        holder.btnEdit.setOnClickListener {
            onEdit(holder.adapterPosition)
        }

        holder.btnDelete.setOnClickListener {
            val context = holder.itemView.context
            val builder = AlertDialog.Builder(context)
            builder.setTitle("Confirm Deletion")
            builder.setMessage("Are you sure you want to delete '${transaction.name}'?")

            builder.setPositiveButton("Yes") { _, _ ->
                Toast.makeText(context, "Deleted ${transaction.name}", Toast.LENGTH_SHORT).show()
                onDelete(holder.adapterPosition)
            }

            builder.setNegativeButton("No") { dialog, _ ->
                dialog.dismiss()
            }

            val dialog = builder.create()
            dialog.show()
        }
    }

    override fun getItemCount(): Int = savingsList.size
}

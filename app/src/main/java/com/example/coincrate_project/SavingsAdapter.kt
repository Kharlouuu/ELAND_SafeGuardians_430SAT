package com.example.coincrate_project

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

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
        val saving = savingsList[position]
        holder.tvName.text = saving.name
        holder.tvAmount.text = "₱%,.2f".format(saving.amount)
        holder.tvType.text = saving.type

        holder.btnEdit.setOnClickListener { onEdit(position) }
        holder.btnDelete.setOnClickListener { onDelete(position) }
    }

    override fun getItemCount(): Int = savingsList.size
}
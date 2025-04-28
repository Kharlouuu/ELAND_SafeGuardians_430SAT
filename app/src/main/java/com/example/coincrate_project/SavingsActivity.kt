package com.example.coincrate_project

import android.os.Bundle
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class SavingsActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: TransactionManageAdapter
    private val savingsList = mutableListOf<Transaction>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.savings_category_page)

        recyclerView = findViewById(R.id.recyclerSavings)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.setHasFixedSize(true)

        adapter = TransactionManageAdapter(savingsList)
        recyclerView.adapter = adapter

        findViewById<ImageButton>(R.id.btnBack).setOnClickListener {
            finish()
        }

        loadSavingsTransactions()
    }

    private fun loadSavingsTransactions() {
        val receivedList = intent.getParcelableArrayListExtra<Transaction>("transactions_list")
        if (receivedList != null) {
            savingsList.clear()
            savingsList.addAll(receivedList.filter { it.type == "Savings" })
            adapter.notifyDataSetChanged()
        }
    }
}
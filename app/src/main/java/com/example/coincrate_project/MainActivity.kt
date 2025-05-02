package com.example.coincrate_project

import android.content.Intent
import android.os.Bundle
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import android.app.Activity
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.activity.result.contract.ActivityResultContracts




class MainActivity : AppCompatActivity() {

    private lateinit var tvTotalSavings: TextView
    private lateinit var rvTransactions: RecyclerView
    private lateinit var transactionsAdapter: TransactionAdapter
    private lateinit var bottomNavigationView: BottomNavigationView

    private val transactionsList = mutableListOf<Transaction>()

    private val addTransactionLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val name = result.data?.getStringExtra("expense_name")
            val amount = result.data?.getStringExtra("expense_amount")?.toDoubleOrNull()
            val type = result.data?.getStringExtra("expense_type")

            if (!name.isNullOrEmpty() && amount != null && !type.isNullOrEmpty()) {
                transactionsList.add(0, Transaction(name, type, amount))

                // Update only the latest 4 items again
                val latestFourTransactions = if (transactionsList.size > 4) {
                    transactionsList.subList(0, 4)
                } else {
                    transactionsList
                }

                // Reset adapter with new data
                transactionsAdapter = TransactionAdapter(latestFourTransactions)
                rvTransactions.adapter = transactionsAdapter
                rvTransactions.scrollToPosition(0)

                val message = "You have successfully added a $type for this month"
                NotificationStorage.addNotification(message)

                saveTransactions()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val btnLogout = findViewById<ImageView>(R.id.btnLogout)
        val btnNotification = findViewById<ImageView>(R.id.btnNotification)
        val btnAddExpense = findViewById<LinearLayout>(R.id.btnAddExpense)
        val btnAddSavings = findViewById<LinearLayout>(R.id.btnAddSavings)
        val btnManage = findViewById<LinearLayout>(R.id.btnManage)
        rvTransactions = findViewById(R.id.rvTransactions)
        bottomNavigationView = findViewById(R.id.bottomNavigation)
        tvTotalSavings = findViewById(R.id.tvTotalSavings)

        transactionsList.addAll(getSampleTransactions())

        // Show only the latest 4 transactions (or fewer if less)
        val latestFourTransactions = if (transactionsList.size > 4) {
            transactionsList.subList(0, 4)
        } else {
            transactionsList
        }

        transactionsAdapter = TransactionAdapter(latestFourTransactions)
        rvTransactions.layoutManager = LinearLayoutManager(this)
        rvTransactions.adapter = transactionsAdapter

        saveTransactions()

        btnLogout.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }

        btnNotification.setOnClickListener {
            Toast.makeText(this, "Notifications clicked", Toast.LENGTH_SHORT).show()
        }

        btnAddExpense.setOnClickListener {
            val intent = Intent(this, AddExpenseActivity::class.java)
            addTransactionLauncher.launch(intent)
        }

        btnAddSavings.setOnClickListener {
            val intent = Intent(this, AddSavingsActivity::class.java)
            addTransactionLauncher.launch(intent)
        }

        btnManage.setOnClickListener {
            startActivity(Intent(this, ManageActivity::class.java))
        }

        btnNotification.setOnClickListener {
            val intent = Intent(this, NotificationActivity::class.java)
            startActivity(intent)
        }

        //Bottom navigation buttons
        bottomNavigationView.setOnItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_home -> true
                R.id.nav_transaction -> {
                    startActivity(Intent(this, TransactionActivity::class.java))
                    true
                }
                R.id.nav_challenges -> {
                    startActivity(Intent(this, ChallengesActivity::class.java))
                    true
                }
                R.id.nav_goals -> {
                    Toast.makeText(this, "Goals clicked", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this, GoalsActivity::class.java))
                    true
                }
                R.id.nav_profile -> {
                    startActivity(Intent(this, HomeActivity::class.java))
                    true
                }
                else -> false
            }
        }
    }
    // Total Savings amount the user input
    override fun onResume() {
        super.onResume()
        val prefs = getSharedPreferences("budget_prefs", MODE_PRIVATE)
        val totalSavings = prefs.getFloat("total_savings", 0.0f).toDouble()
        tvTotalSavings.text = "₱%,.2f".format(totalSavings)
    }


    // Sample Transaction Display in Dashboard
    private fun getSampleTransactions(): List<Transaction> {
        return listOf(
            Transaction("Groceries", "Expenses", 5000.00),
            Transaction("Water bill", "Expenses", 800.00),
            Transaction("Emergency Fund", "Savings", 1500.00)
        )
    }
    private fun saveTransactions() {
        val prefs = getSharedPreferences("transactions_prefs", MODE_PRIVATE)
        val editor = prefs.edit()

        val transactionsString = transactionsList.joinToString("#") { "${it.name}|${it.type}|${it.amount}" }
        editor.putString("transactions_list", transactionsString)
        editor.apply()
    }

    data class Transaction(val name: String, val type: String, val amount: Double)

    class TransactionAdapter(private val transactions: List<Transaction>) :
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

            // Set color based on type
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
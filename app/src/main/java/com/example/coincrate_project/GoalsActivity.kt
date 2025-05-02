package com.example.coincrate_project

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class GoalsActivity : AppCompatActivity() {
    private lateinit var db: AppDatabase
    private lateinit var goalDao: GoalDao
    private lateinit var savedDayDao: SavedDayDao
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: GoalAdapter
    private lateinit var btnBack: ImageView
    private lateinit var btnAddGoal: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_goals)

        // Initialize views
        btnBack = findViewById(R.id.btnBack)
        btnAddGoal = findViewById(R.id.btnAddGoal)
        recyclerView = findViewById(R.id.rvGoals)

        db = AppDatabase.getDatabase(this)
        goalDao = db.goalDao()
        savedDayDao = db.savedDayDao()

        adapter = GoalAdapter()
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        btnBack.setOnClickListener {
            finish()
        }

        btnAddGoal.setOnClickListener {
            val intent = Intent(this, AddGoalActivity::class.java)
            startActivity(intent)
        }

        setupSwipeToDelete()
        updateGoals()
    }

    private fun setupSwipeToDelete() {
        val itemTouchHelper = ItemTouchHelper(object :
            ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {

            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean = false

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                val currentGoals = adapter.currentList
                if (position in currentGoals.indices) {
                    val goalToDelete = currentGoals[position]

                    // Confirmation dialog
                    AlertDialog.Builder(this@GoalsActivity)
                        .setTitle("Delete Goal")
                        .setMessage("Are you sure you want to delete '${goalToDelete.name}'?")
                        .setPositiveButton("Delete") { _, _ ->
                            lifecycleScope.launch {
                                goalDao.delete(goalToDelete)
                                updateGoals()
                            }
                        }
                        .setNegativeButton("Cancel") { _, _ ->
                            adapter.notifyItemChanged(position)
                        }
                        .setCancelable(false)
                        .show()
                }
            }
        })
        itemTouchHelper.attachToRecyclerView(recyclerView)
    }

    private fun updateGoals() {
        lifecycleScope.launch {
            val goals = goalDao.getAllGoals()
            val totalSaved = getMonthlyTotal()

            val updatedGoals = goals.map {
                val achieved = totalSaved >= it.amount
                if (achieved != it.isAchieved) {
                    goalDao.update(it.copy(isAchieved = achieved))
                }
                it.copy(isAchieved = achieved)
            }

            withContext(Dispatchers.Main) {
                adapter.submitList(updatedGoals)
            }
        }
    }

    private suspend fun getMonthlyTotal(): Double {
        val now = LocalDate.now()
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        val start = now.withDayOfMonth(1).format(formatter)
        val end = now.withDayOfMonth(now.lengthOfMonth()).format(formatter)
        val count = savedDayDao.getCountInRange(start, end)

        val sharedPref = getSharedPreferences("coincrate_prefs", MODE_PRIVATE)
        val dailyGoal = sharedPref.getFloat("dailyGoal", 0.0f)
        return count * dailyGoal.toDouble()
    }
}
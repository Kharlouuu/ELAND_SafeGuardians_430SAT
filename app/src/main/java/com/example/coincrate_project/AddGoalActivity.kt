package com.example.coincrate_project

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AddGoalActivity : AppCompatActivity() {
    private lateinit var db: AppDatabase
    private lateinit var goalDao: GoalDao
    private lateinit var etGoalName: EditText
    private lateinit var etAmount: EditText
    private lateinit var btnSave: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_goal)

        db = AppDatabase.getDatabase(this)
        goalDao = db.goalDao()

        etGoalName = findViewById(R.id.etGoalName)
        etAmount = findViewById(R.id.etGoalAmount)
        btnSave = findViewById(R.id.btnSave)

        btnSave.setOnClickListener {
            val name = etGoalName.text.toString()
            val amount = etAmount.text.toString().toDoubleOrNull() ?: 0.0

            if (name.isNotEmpty() && amount > 0.0) {
                lifecycleScope.launch {
                    goalDao.insert(GoalEntity(name = name, amount = amount))
                    withContext(Dispatchers.Main) {
                        Toast.makeText(this@AddGoalActivity, "Goal added!", Toast.LENGTH_SHORT).show()
                        finish()
                    }
                }
            } else {
                Toast.makeText(this, "Please enter valid name and amount.", Toast.LENGTH_SHORT).show()
            }
        }
    }
}

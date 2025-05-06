package com.example.coincrate_project

import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.prolificinteractive.materialcalendarview.MaterialCalendarView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import com.prolificinteractive.materialcalendarview.CalendarDay

class ChallengesActivity : AppCompatActivity() {

    private lateinit var calendarView: MaterialCalendarView
    private lateinit var db: AppDatabase
    private lateinit var savedDayDao: SavedDayDao
    private lateinit var etDailyGoal: EditText
    private lateinit var btnSetGoal: ImageView
    private lateinit var btnSaveToday: Button
    private lateinit var tvStreak: TextView
    private lateinit var tvMonthlyTotal: TextView
    private lateinit var tvDailyGoalDisplay: TextView
    private lateinit var cardSuccess: View

    private var dailyGoal: Double = 0.0
    private val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_challenges)


        val btnBack = findViewById<ImageView>(R.id.btnBack)
        btnBack.setOnClickListener { finish() }

        db = AppDatabase.getDatabase(applicationContext)
        savedDayDao = db.savedDayDao()

        calendarView = findViewById(R.id.calendarView)
        etDailyGoal = findViewById(R.id.etDailyGoal)
        btnSetGoal = findViewById(R.id.btnSetGoal)
        btnSaveToday = findViewById(R.id.btnSaveToday)
        tvStreak = findViewById(R.id.tvStreak)
        tvMonthlyTotal = findViewById(R.id.tvMonthlyTotal)
        tvDailyGoalDisplay = findViewById(R.id.tvDailyGoal)
        cardSuccess = findViewById(R.id.cardSavedToday)
        cardSuccess.visibility = View.GONE

        val prefs = getSharedPreferences("coincrate_prefs", MODE_PRIVATE)
        val hasLaunched = prefs.getBoolean("app_launched_once", false)

        if (!hasLaunched) {
            // First cold start - reset everything
            prefs.edit().putBoolean("app_launched_once", true).apply()
            prefs.edit().remove("dailyGoal").apply()
            dailyGoal = 0.0
            tvDailyGoalDisplay.text = "Daily Goal\n₱%.2f".format(dailyGoal)

            lifecycleScope.launch {
                withContext(Dispatchers.IO) {
                    savedDayDao.deleteAll()
                }
                updateStreak()
                updateMonthlyTotal()
            }
        } else {
            // Load existing daily goal
            dailyGoal = prefs.getFloat("dailyGoal", 0.0f).toDouble()
            tvDailyGoalDisplay.text = "Daily Goal\n₱%.2f".format(dailyGoal)

            // Update UI from existing DB
            lifecycleScope.launch {
                updateStreak()
                updateMonthlyTotal()
            }
        }

        btnSaveToday.setOnClickListener {
            if (dailyGoal <= 0.0) {
                Toast.makeText(
                    this,
                    "Please set a valid daily goal amount before saving.",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            val today = LocalDate.now()

            lifecycleScope.launch {
                if (savedDayDao.getByDate(today.toString()) == null) {
                    savedDayDao.insert(SavedDay(date = today.toString(), amountSaved = dailyGoal))
                    withContext(Dispatchers.Main) {
                        Toast.makeText(
                            this@ChallengesActivity,
                            "You have successfully saved today",
                            Toast.LENGTH_SHORT
                        ).show()
                        calendarView.setDateSelected(CalendarDay.from(today.year, today.monthValue - 1, today.dayOfMonth), true)
                        updateStreak()
                        updateMonthlyTotal()
                        cardSuccess.visibility = View.VISIBLE
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(
                            this@ChallengesActivity,
                            "Already saved for today!",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }

        btnSetGoal.setOnClickListener {
            val input = etDailyGoal.text.toString()
            dailyGoal = input.toDoubleOrNull() ?: 0.0
            tvDailyGoalDisplay.text = "Daily Goal\n₱%.2f".format(dailyGoal)

            // Save daily goal to SharedPreferences
            prefs.edit().putFloat("dailyGoal", dailyGoal.toFloat()).apply()

            Toast.makeText(this, "Daily goal set to ₱$dailyGoal", Toast.LENGTH_SHORT).show()
            lifecycleScope.launch {
                updateMonthlyTotal()
            }
        }
    }

    private suspend fun updateStreak() {
        val today = LocalDate.now()
        var streak = 0
        for (i in 0..30) {
            val date = today.minusDays(i.toLong()).format(dateFormatter)
            if (savedDayDao.getByDate(date) != null) {
                streak++
            } else break
        }
        withContext(Dispatchers.Main) {
            tvStreak.text = "Streak: $streak days"
        }
    }

    private suspend fun updateMonthlyTotal() {
        val now = LocalDate.now()
        val start = now.withDayOfMonth(1)
        val end = now.withDayOfMonth(now.lengthOfMonth())
        val count = savedDayDao.getCountInRange(start.format(dateFormatter), end.format(dateFormatter))
        val total = count * dailyGoal

        withContext(Dispatchers.Main) {
            tvMonthlyTotal.text = "₱%.2f".format(total)
        }
    }
}
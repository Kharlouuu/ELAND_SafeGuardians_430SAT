package com.example.coincrate_project

import android.os.Bundle
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

    private var dailyGoal: Double = 0.0
    private val savedDates = mutableSetOf<String>()
    private val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_challenges)

        db = AppDatabase.getDatabase(applicationContext)
        savedDayDao = db.savedDayDao()

        calendarView = findViewById(R.id.calendarView)
        etDailyGoal = findViewById(R.id.etDailyGoal)
        btnSetGoal = findViewById(R.id.btnSetGoal)
        btnSaveToday = findViewById(R.id.btnSaveToday)
        tvStreak = findViewById(R.id.tvStreak)
        tvMonthlyTotal = findViewById(R.id.tvMonthlyTotal)
        tvDailyGoalDisplay = findViewById(R.id.tvDailyGoal) // Make sure this exists in XML

        lifecycleScope.launch {
            val allSaved = savedDayDao.getAll()
            savedDates.addAll(allSaved.map { it.date })

            allSaved.forEach {
                val date = LocalDate.parse(it.date, dateFormatter)
                calendarView.setDateSelected(CalendarDay.from(date.year, date.monthValue, date.dayOfMonth), true)
            }

            updateStreak()
            updateMonthlyTotal()
        }

        btnSaveToday.setOnClickListener {
            if (dailyGoal <= 0.0) {
                Toast.makeText(
                    this@ChallengesActivity,
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
                        calendarView.setDateSelected(CalendarDay.from(today.year, today.monthValue, today.dayOfMonth), true)
                        updateStreak()
                        updateMonthlyTotal()
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
            Toast.makeText(this, "Daily goal set to ₱$dailyGoal", Toast.LENGTH_SHORT).show()
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

        val count = savedDayDao.getCountInRange(
            start.format(dateFormatter),
            end.format(dateFormatter)
        )

        val total = count * dailyGoal

        withContext(Dispatchers.Main) {
            tvMonthlyTotal.text = "₱%.2f".format(total)
            //  Debug log to check if range count works
            Toast.makeText(
                this@ChallengesActivity,
                "Saved days this month: $count",
                Toast.LENGTH_SHORT
            ).show()
        }
    }
}
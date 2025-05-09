package com.example.coincrate_project

import android.content.Intent
import android.os.Bundle
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView

open class BaseActivity : AppCompatActivity() {

    override fun setContentView(layoutResID: Int) {
        // Inflate the base layout
        val fullView = layoutInflater.inflate(R.layout.activity_base, null)

        // Find the container inside the base layout and inflate the child layout into it
        val activityContainer = fullView.findViewById<FrameLayout>(R.id.container)
        layoutInflater.inflate(layoutResID, activityContainer, true)

        // Set the content view to the composed view
        super.setContentView(fullView)

        // Set up the bottom navigation listener
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNavigation)

        bottomNav.setOnItemSelectedListener { menuItem ->
            val currentActivity = this::class.java

            when (menuItem.itemId) {
                R.id.nav_home -> {
                    if (currentActivity != MainActivity::class.java) {
                        startActivity(Intent(this, MainActivity::class.java))
                        finish()
                    }
                    true
                }
                R.id.nav_transaction -> {
                    if (currentActivity != TransactionActivity::class.java) {
                        startActivity(Intent(this, TransactionActivity::class.java))
                        finish()
                    }
                    true
                }
                R.id.nav_challenges -> {
                    if (currentActivity != ChallengesActivity::class.java) {
                        startActivity(Intent(this, ChallengesActivity::class.java))
                        finish()
                    }
                    true
                }
                R.id.nav_goals -> {
                    if (currentActivity != GoalsActivity::class.java) {
                        startActivity(Intent(this, GoalsActivity::class.java))
                        finish()
                    }
                    true
                }
                R.id.nav_profile -> {
                    if (currentActivity != ProfileActivity::class.java) {
                        startActivity(Intent(this, ProfileActivity::class.java))
                        finish()
                    }
                    true
                }
                else -> false
            }
        }
    }
}

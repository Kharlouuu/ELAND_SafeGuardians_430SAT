package com.example.coincrate_project

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity

class SplashScreenActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.splash_screen)

        // Set up a handler to delay for 3 seconds and then navigate to LoginActivity
        Handler().postDelayed({
            // After 3 seconds, navigate to LoginActivity
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish() // Finish the splash screen so the user cannot go back to it
        }, 3000) // 3000 milliseconds = 3 seconds
    }
}

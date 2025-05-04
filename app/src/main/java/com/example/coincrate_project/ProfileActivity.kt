package com.example.coincrate_project

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class ProfileActivity : AppCompatActivity() {

    private lateinit var tvUserName: TextView
    private lateinit var tvUserEmail: TextView
    private lateinit var btnEditInfo: Button
    private lateinit var btnLogout: Button
    private lateinit var btnBack: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        tvUserName = findViewById(R.id.tvUserName)
        tvUserEmail = findViewById(R.id.tvUserEmail)
        btnEditInfo = findViewById(R.id.btnEditInfo)
        btnLogout = findViewById(R.id.btnLogout)
        btnBack = findViewById(R.id.btnBack)

        val sharedPref = getSharedPreferences("user_prefs", MODE_PRIVATE)
        val username = sharedPref.getString("username", "Unknown User")
        val email = sharedPref.getString("email", "unknown@email.com")

        tvUserName.text = username
        tvUserEmail.text = email

        btnEditInfo.setOnClickListener {
            val intent = Intent(this, EditProfileActivity::class.java)
            intent.putExtra("username", username)
            intent.putExtra("email", email)
            startActivity(intent)
        }

        btnLogout.setOnClickListener {
            sharedPref.edit().clear().apply()
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }

        btnBack.setOnClickListener {
            finish()
        }
    }

    override fun onResume() {
        super.onResume()
        val sharedPref = getSharedPreferences("user_prefs", MODE_PRIVATE)
        tvUserName.text = sharedPref.getString("username", "Unknown User")
        tvUserEmail.text = sharedPref.getString("email", "unknown@email.com")
    }
}
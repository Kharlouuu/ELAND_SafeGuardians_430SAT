package com.example.coincrate_project

import android.os.Bundle
import android.view.WindowInsetsAnimation
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class EditProfileActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)

        val etUsername = findViewById<EditText>(R.id.etUsername)
        val etEmail = findViewById<EditText>(R.id.etEmail)
        val etNewPassword = findViewById<EditText>(R.id.etNewPassword)
        val etConfirmPassword = findViewById<EditText>(R.id.etConfirmPassword)
        val btnSave = findViewById<Button>(R.id.btnSave)

        // Get current user info from SharedPreferences
        val sharedPref = getSharedPreferences("user_prefs", MODE_PRIVATE)
        val email = sharedPref.getString("email", "") ?: ""
        val currentUsername = sharedPref.getString("username", "") ?: ""

        etEmail.setText(email)
        etEmail.isEnabled = false
        etUsername.setText(currentUsername)

        btnSave.setOnClickListener {
            val newUsername = etUsername.text.toString().trim()
            val newPassword = etNewPassword.text.toString().trim()
            val confirmPassword = etConfirmPassword.text.toString().trim()

            if (newUsername.isEmpty()) {
                Toast.makeText(this, "Username cannot be empty", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (newPassword.isNotEmpty() && newPassword != confirmPassword) {
                Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Call update API
            RetrofitInstance.api.updateUser(email, newUsername, newPassword)
                .enqueue(object : Callback<ResponseBody> {
                    override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                        if (response.isSuccessful) {
                            sharedPref.edit().putString("username", newUsername).apply()
                            Toast.makeText(this@EditProfileActivity, "Profile updated", Toast.LENGTH_SHORT).show()
                            finish()
                        } else {
                            Toast.makeText(this@EditProfileActivity, "Failed to update profile", Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                        Toast.makeText(this@EditProfileActivity, "Network error: ${t.message}", Toast.LENGTH_SHORT).show()
                    }
                })
        }
    }
}
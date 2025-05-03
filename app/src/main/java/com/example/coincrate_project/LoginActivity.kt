package com.example.coincrate_project

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login_page)

        val etEmail = findViewById<EditText>(R.id.etEmail)
        val etPassword = findViewById<EditText>(R.id.etPassword)
        val btnLogin = findViewById<Button>(R.id.btnLogin)
        val tvForgotPassword = findViewById<TextView>(R.id.tvForgotPassword)
        val tvRegister = findViewById<TextView>(R.id.tvRegister)

        tvRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }

        tvForgotPassword.setOnClickListener {
            startActivity(Intent(this, ForgotPasswordActivity::class.java))
        }

        btnLogin.setOnClickListener {
            val email = etEmail.text.toString().trim()
            val password = etPassword.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            RetrofitInstance.api.login(email, password).enqueue(object : Callback<ResponseBody> {
                override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                    if (response.isSuccessful && response.body() != null) {
                        val bodyString = response.body()!!.string()
                        Log.d("RawLoginResponse", bodyString)

                        try {
                            val json = JSONObject(bodyString)
                            if (json.has("success")) {
                                val username = json.optString("username")
                                val userEmail = json.optString("email")

                                // Save to SharedPreferences
                                val sharedPref = getSharedPreferences("user_prefs", MODE_PRIVATE)
                                sharedPref.edit()
                                    .putString("username", username)
                                    .putString("email", userEmail)
                                    .apply()

                                Toast.makeText(this@LoginActivity, "Welcome $username!", Toast.LENGTH_SHORT).show()
                                startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                                finish()
                            } else {
                                val errorMessage = json.optString("error", "Login failed")
                                Toast.makeText(this@LoginActivity, errorMessage, Toast.LENGTH_SHORT).show()
                            }

                        } catch (e: Exception) {
                            Log.e("LoginParseError", "Invalid JSON", e)
                            Toast.makeText(this@LoginActivity, "Login failed: Invalid server response", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Toast.makeText(this@LoginActivity, "Login failed", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    Log.e("LoginError", "Failed to connect", t)
                    Toast.makeText(this@LoginActivity, "Failed: ${t.message}", Toast.LENGTH_LONG).show()
                }
            })
        }
    }
}
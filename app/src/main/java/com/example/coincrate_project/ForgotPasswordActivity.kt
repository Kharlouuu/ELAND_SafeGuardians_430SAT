package com.example.coincrate_project

import android.annotation.SuppressLint
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

class ForgotPasswordActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.forgotpass_page)

        val etEmail = findViewById<EditText>(R.id.etEmail)
        val btnSendCode = findViewById<Button>(R.id.btnSendCode)
        val tvGoBack = findViewById<TextView>(R.id.tvGoBack)

        btnSendCode.setOnClickListener {
            val email = etEmail.text.toString().trim()

            if (email.isEmpty()) {
                Toast.makeText(this, "Email is required", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Validate Gmail address
            val gmailRegex = Regex("^[A-Za-z0-9+_.-]+@gmail\\.com$")
            if (!gmailRegex.matches(email)) {
                Toast.makeText(this, "Please enter a valid Gmail address", Toast.LENGTH_SHORT)
                    .show()
                return@setOnClickListener
            }

            RetrofitInstance.api.forgotPassword(email).enqueue(object : Callback<ResponseBody> {
                override fun onResponse(
                    call: Call<ResponseBody>,
                    response: Response<ResponseBody>
                ) {
                    val responseString = response.body()?.string()
                    Log.d("ForgotPassword", "Response: $responseString")

                    if (responseString != null) {
                        try {
                            val json = JSONObject(responseString)
                            val success = json.optString("success", null)
                            val error = json.optString("error", null)

                            if (success != null) {
                                Toast.makeText(
                                    this@ForgotPasswordActivity,
                                    "Code sent to your email",
                                    Toast.LENGTH_SHORT
                                ).show()

                                val intent = Intent(
                                    this@ForgotPasswordActivity,
                                    ResetPasswordActivity::class.java
                                )
                                intent.putExtra("email", email)
                                startActivity(intent)
                            } else {
                                Toast.makeText(
                                    this@ForgotPasswordActivity,
                                    error ?: "Failed to send code",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        } catch (e: Exception) {
                            Log.e("ForgotPassParse", "JSON parsing error: ${e.message}")
                            Toast.makeText(
                                this@ForgotPasswordActivity,
                                "Invalid response from server",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    } else {
                        Toast.makeText(
                            this@ForgotPasswordActivity,
                            "Empty server response",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    Log.e("ForgotPass", "Network Error: ${t.message}", t)
                    Toast.makeText(
                        this@ForgotPasswordActivity,
                        "Network error: ${t.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            })
        }

        tvGoBack.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }
}
package com.example.coincrate_project

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ResetPasswordActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.reset_password)

        val etEmail = findViewById<EditText>(R.id.etEmail)
        val etResetCode = findViewById<EditText>(R.id.etResetCode)
        val etNewPassword = findViewById<EditText>(R.id.etNewPassword)
        val btnResetPassword = findViewById<Button>(R.id.btnResetPassword)

        // Autofill email from intent and disable editing
        val passedEmail = intent.getStringExtra("email")
        etEmail.setText(passedEmail)
        etEmail.isEnabled = false // disables editing

        btnResetPassword.setOnClickListener {
            val email = etEmail.text.toString().trim()
            val code = etResetCode.text.toString().trim()
            val newPassword = etNewPassword.text.toString().trim()

            if (code.isEmpty() || newPassword.isEmpty()) {
                Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            RetrofitInstance.api.resetPassword(email, code, newPassword)
                .enqueue(object : Callback<ResponseBody> {
                    override fun onResponse(
                        call: Call<ResponseBody>,
                        response: Response<ResponseBody>
                    ) {
                        if (response.isSuccessful && response.body() != null) {
                            val json = response.body()?.string()
                            val jsonObject = JSONObject(json ?: "")

                            if (jsonObject.has("success")) {
                                Toast.makeText(
                                    this@ResetPasswordActivity,
                                    "Password changed successfully",
                                    Toast.LENGTH_SHORT
                                ).show()
                                startActivity(
                                    Intent(
                                        this@ResetPasswordActivity,
                                        LoginActivity::class.java
                                    )
                                )
                                finish()
                            } else if (jsonObject.has("error")) {
                                Toast.makeText(
                                    this@ResetPasswordActivity,
                                    jsonObject.getString("error"),
                                    Toast.LENGTH_SHORT
                                ).show()
                            } else {
                                Toast.makeText(
                                    this@ResetPasswordActivity,
                                    "Unexpected server response",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        } else {
                            Toast.makeText(
                                this@ResetPasswordActivity,
                                "Server error",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }

                    override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                        Toast.makeText(
                            this@ResetPasswordActivity,
                            "Network error: ${t.message}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                })
        }
    }
}
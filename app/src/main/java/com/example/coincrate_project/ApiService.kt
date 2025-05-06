package com.example.coincrate_project

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*

interface ApiService {

    // Register a new user
    @FormUrlEncoded
    @POST("backend.php?action=register")
    fun register(
        @Field("username") username: String,
        @Field("email") email: String,
        @Field("password") password: String
    ): Call<ResponseBody>

    // Update the Profile
    @FormUrlEncoded
    @POST("backend.php?action=update_profile")
    fun updateUser(
        @Field("email") email: String,
        @Field("new_username") newUsername: String,
        @Field("new_password") newPassword: String
    ): Call<ResponseBody>

    // User login
    @FormUrlEncoded
    @POST("backend.php?action=login")
    fun login(
        @Field("email") email: String,
        @Field("password") password: String
    ): Call<ResponseBody>

    // Forgot password
    @FormUrlEncoded
    @POST("backend.php?action=forgot_password")
    fun forgotPassword(
        @Field("email") email: String
    ): Call<ResponseBody>

    // Reset password
    @FormUrlEncoded
    @POST("backend.php?action=reset_password")
    fun resetPassword(
        @Field("email") email: String,
        @Field("reset_code") resetCode: String,
        @Field("new_password") newPassword: String
    ): Call<ResponseBody>
  
      // Optional: Response model
    data class ApiResponse(
        val success: String? = null,
        val error: String? = null
    )
}
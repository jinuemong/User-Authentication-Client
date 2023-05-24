package com.example.user_authentication.Server

import com.example.user_authentication.Model.Token
import retrofit2.http.GET

interface TokenObtainApi {
    @GET("user/auth/token")
    suspend fun getToken() : Token
}
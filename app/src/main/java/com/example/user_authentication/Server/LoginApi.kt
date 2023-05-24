package com.example.user_authentication.Server

import com.example.user_authentication.Model.GetUser
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface LoginApi {

    @POST("user/login/")
    @FormUrlEncoded
    suspend fun login(
        @Field("username") username : String,
        @Field("password") password : String
    ) : GetUser

}
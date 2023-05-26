package com.example.user_authentication.Server

import com.example.user_authentication.Model.GetUser
import com.example.user_authentication.Model.User
import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.PATCH
import retrofit2.http.POST

interface RetrofitService {

    //로그인 요청
    @POST("user/login/")
    @FormUrlEncoded
    fun loginUser(
        @Field("username") username: String,
        @Field("password") password: String
    ): Call<GetUser>

    // 가입
    @POST("user/register/")
    @FormUrlEncoded
    fun registerUser(
        @Field("username") username: String,
        @Field("password") password: String
    ): Call<GetUser>

    //비밀번호 수정
    @PATCH("user/current/")
    @FormUrlEncoded
    fun updatePassword(
        @Field("username") username: String,
        @Field("password") password: String
    ): Call<User>
}
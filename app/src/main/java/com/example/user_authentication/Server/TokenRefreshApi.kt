package com.example.user_authentication.Server

import com.example.user_authentication.Model.Token
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface TokenRefreshApi {
    @POST("user/auth/refresh/")
    @FormUrlEncoded
    suspend fun patchToken(
        @Field("refresh") refresh : String
    ) : Token
}
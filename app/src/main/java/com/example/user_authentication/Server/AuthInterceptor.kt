package com.example.user_authentication.Server

import android.content.Context
import android.content.Intent
import com.example.user_authentication.LoginActivity
import com.example.user_authentication.MainActivity
import com.example.user_authentication.Model.Token
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response

// when an error occurs
//intercept the communication
// like token Regeneration or refresh
// create a new task

class AuthInterceptor(
    private val context: Context,
    private val tokenApi : TokenRefreshApi,
    private val obtainApi: TokenObtainApi,
):Interceptor , BaseRepository() {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val response = chain.proceed(request)

        // 401 error : No Authentication Credentials
        if (response.code == 401 ){
            return runBlocking {
                when (val token = getUpdateToken()){
                    is Resource.Success -> {
                        // refresh token
                        val sp = context.getSharedPreferences("login_sp",Context.MODE_PRIVATE)
                        val editor = sp.edit()
                        val accessToken = token.value!!.access
                        val refreshToken = token.value.refresh
                        editor.putString("accessToken",accessToken)
                        editor.putString("refreshToken",refreshToken)
                        editor.apply()

                        // delete prev token, new response(new token) return
                        val newRequest = chain.request().newBuilder().removeHeader("Authorization")
                        newRequest.addHeader("Authorization","Bearer $accessToken")
                        return@runBlocking chain.proceed(newRequest.build())
                    }
                    else -> {
                        when (val newToken = newToken()){
                            is Resource.Success ->{
                                // if fail refresh token -> make new token api
                                val sp = context.getSharedPreferences("login_sp",Context.MODE_PRIVATE)
                                val editor = sp.edit()
                                val accessToken = newToken.value!!.access
                                val refreshToken = newToken.value.refresh
                                editor.putString("accessToken", accessToken)
                                editor.putString("refreshToken", refreshToken)
                                editor.apply()
                                // delete prev token, new response(new token) return
                                val newRequest = chain.request().newBuilder().removeHeader("Authorization")
                                newRequest.addHeader("Authorization", "Bearer $accessToken")
                                return@runBlocking chain.proceed(newRequest.build())
                            }
                            else -> {
                                // fail all case -> logout
                                val intent = Intent(context, LoginActivity::class.java)
                                intent.putExtra("logout",true)
                                context.startActivity(intent)
                            }
                        }
                        return@runBlocking response
                    }
                }
            }
        }

        // return normal request
        return response
    }

    private suspend fun getUpdateToken() : Resource<Token?>{
        val refreshToken = context.getSharedPreferences("login_sp",Context.MODE_PRIVATE)
            .getString("refreshToken","").toString()
        // api request :  for safeApiCall
        // If the token does not exist -> null
        return safeApiCall { tokenApi.patchToken(refreshToken) }
    }

    private suspend fun newToken() : Resource<Token?>{
        // new refresh + access token
        return safeApiCall { obtainApi.getToken() }
    }
}
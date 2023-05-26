package com.example.user_authentication.Server

import android.app.Activity
import android.app.Application
import android.content.Context
import com.facebook.stetho.Stetho
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory

class MasterApplication : Application() {

    var service: RetrofitService? = null
    // ngrok
    private val baseUrl = "https://196b-14-51-88-88.ngrok-free.app"
    var activity  : Activity? = null

    override fun onCreate() {
        super.onCreate()
        Stetho.initializeWithDefaults(this)
    }

    fun createRetrofit(activity: Activity){
        this.activity = activity
        val header = Interceptor{
            val original = it.request()
            if (checkIsLogin()){
                // access token is exist -> add token to header
                getUserToken().let {token->
                    val request = original.newBuilder()
                        .header("Authorization","token $token")
                        .build()
                    it.proceed(request)
                }
            }else{
                // else -> original request
                it.proceed(original)
            }
        }

        val retrofit = Retrofit.Builder()
            .baseUrl("$baseUrl/")
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .client(getRetrofitClient(header))
            .build()
        service = retrofit.create(RetrofitService::class.java)
    }

    // create client for retrofit service
    private fun getRetrofitClient(header:Interceptor) : OkHttpClient{
        return OkHttpClient.Builder()
            .addInterceptor {chain ->
                chain.proceed(chain.request().newBuilder().also {
                    it.addHeader("Accept","application/json")
                }.build())
            }.also { client ->
                client.addInterceptor(header)
                client.addInterceptor(AuthInterceptor(activity!!,buildTokenApi(),obtainTokenApi()))

                // Add interceptor to check log information
                val logInterceptor = HttpLoggingInterceptor()
                logInterceptor.level = HttpLoggingInterceptor.Level.BODY
                client.addInterceptor(logInterceptor)
            }.build()
    }

    // Create a temporary client to request refresh token
    private fun buildTokenApi() : TokenRefreshApi {
        val client = OkHttpClient.Builder().build()
        return Retrofit.Builder()
            .baseUrl("$baseUrl/")
            .client(client)
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(TokenRefreshApi::class.java)
    }

    // Create a temporary client to request obtain token
    private fun obtainTokenApi() : TokenObtainApi {
        val client = OkHttpClient.Builder().build()
        return Retrofit.Builder()
            .baseUrl("$baseUrl/")
            .client(client)
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(TokenObtainApi::class.java)
    }

    // check : access token is null
    private fun checkIsLogin() : Boolean{
        val sp = getSharedPreferences("login_sp", Context.MODE_PRIVATE)
        val token = sp.getString("accessToken","null")
        return token!="null"
    }

    // get : login user token
    private fun getUserToken() : String?{
        val sp = getSharedPreferences("login_sp",Context.MODE_PRIVATE)
        val token = sp.getString("accessToken","null")
        return if (token=="null") null
        else token
    }
}
package com.example.user_authentication

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.SharedPreferences.Editor
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.example.user_authentication.Model.GetUser
import com.example.user_authentication.Model.Token
import com.example.user_authentication.Server.MasterApplication
import com.example.user_authentication.databinding.ActivityLoginBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : AppCompatActivity() {
    private var backPressTime : Long = 0 // to count back button double click
    private lateinit var callback : OnBackPressedCallback
    private lateinit var sharedPreferences : SharedPreferences
    private lateinit var editor : Editor
    private lateinit var binding : ActivityLoginBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // If the back action is pressed twice in succession
        callback = object  : OnBackPressedCallback(true){
            override fun handleOnBackPressed() {
                if (System.currentTimeMillis()>backPressTime+2000){
                    backPressTime = System.currentTimeMillis()
                    Toast.makeText(applicationContext,"Pressing the back button once more closes the app."
                        , Toast.LENGTH_SHORT).show()
                } else{
                    finishAffinity()
                }
            }
        }
        this.onBackPressedDispatcher.addCallback(this,callback)

        // Encrypted storage for automatic login
        //encrypt auto-login data
        //must be declared inside on create()
        val masterKeyAlias = MasterKey
            .Builder(applicationContext, MasterKey.DEFAULT_MASTER_KEY_ALIAS)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()

        sharedPreferences =
            EncryptedSharedPreferences.create(
                applicationContext,
                "encrypted_settings", // store file name
                masterKeyAlias,
                EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            )
        editor = sharedPreferences.edit()

        // if user logout -> Delete auto-login data
        val isLogout = intent.getBooleanExtra("logout",false)
        if (isLogout){
            // Delete data if it exists
            setShardLogin()
        }

        // if auto login check? -> Try auto-login
        if (checkAutoData()){
            binding.autoLoginCheckBox.isChecked = true
            setUserName(sharedPreferences.getString("name",""))
            setUserPass(sharedPreferences.getString("pass",""))
            login()
        }

        // go login
        binding.login.setOnClickListener {
            login()
        }

        // go register
        binding.insert.setOnClickListener {
            val insertBox = InsertFragment()
            insertBox.show(this@LoginActivity.supportFragmentManager,null)
        }
    }


    private fun getUserName() : String{
        return binding.userName.text.toString()
    }

    private fun getUserPassword() : String{
        return binding.pw.text.toString()
    }

    private fun setUserName(name : String?){
        if (name!="" && name != null){
            binding.userName.setText(name)
        }
    }
    private fun setUserPass(pass : String?){
        if (pass!="" && pass != null){
            binding.pw.setText(pass)
        }
    }

    // login user
    private fun login(){
        val masterApp = (this@LoginActivity.application as MasterApplication)
        masterApp.createRetrofit(this@LoginActivity)
        masterApp.service?.let {
            it.loginUser(
                getUserName(),getUserPassword()
            ).enqueue(object : Callback<GetUser> {
                override fun onResponse(call: Call<GetUser>, response: Response<GetUser>) {
                    if (response.isSuccessful && response.body()!=null) {

                        // success Login
                        val authUser = response.body()!!.user
                        val token = authUser.token

                        // save token
                        saveUserToken(token,this@LoginActivity)

                        // if auto login check ? -> set auto Login
                        setShardLogin()

                        // go main
                        val intent = Intent(applicationContext,MainActivity::class.java)
                        intent.putExtra("username",getUserName())
                        startActivity(intent)
                    }else{
                        failLogin(response.errorBody()!!.string().substring(0,10))
                    }
                }

                override fun onFailure(call: Call<GetUser>, t: Throwable) {
                    failLogin(t.toString())
                }

            })
        }
    }

    //save auto login data
    private fun saveAutoData(){
        editor.apply{
            putString("name",getUserName())
            putString("pass",getUserPassword())
            commit()
        }
    }

    // remove auto login data
    private fun removeAutoData(){
        editor.apply {
            remove("name")
            remove("pass")
            clear()
            commit()
        }
    }

    // check auto data
    private fun checkAutoData() : Boolean{
        return sharedPreferences.getString("name","") !=""
                && sharedPreferences.getString("pass","")!=""
    }

    // check auto login
    private fun setShardLogin(){
        // if auto-login checkbox is check?
        if (binding.autoLoginCheckBox.isChecked){
            // auto data not saved ?- > new data save
            if (!checkAutoData()){
                saveAutoData()
            }
        }
        // if check out ? -> removeAuto data
        else {
            if (checkAutoData()){
                removeAutoData()
            }
        }
    }

    @SuppressLint("CommitPrefEdits")
    private fun saveUserToken(token : Token, activity: LoginActivity){
        val sp = activity.getSharedPreferences("login_sp",Context.MODE_PRIVATE)
        val editor = sp.edit()
        editor.apply {
            putString("accessToken",token.access)
            putString("refreshToekn",token.refresh)
            apply()
        }
    }

    private fun failLogin(message:String){
        Toast.makeText(this@LoginActivity,message,Toast.LENGTH_SHORT)
            .show()
    }

}
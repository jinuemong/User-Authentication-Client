package com.example.user_authentication

import android.content.SharedPreferences
import android.content.SharedPreferences.Editor
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import com.example.user_authentication.databinding.ActivityLoginBinding

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
    }
}
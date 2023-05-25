package com.example.user_authentication

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.user_authentication.databinding.ActivityLoginBinding
import com.example.user_authentication.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private var backPressTime : Long = 0 // to count back button double click
    private lateinit var binding : ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}
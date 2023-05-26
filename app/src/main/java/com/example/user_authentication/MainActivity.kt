package com.example.user_authentication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import com.example.user_authentication.databinding.ActivityLoginBinding
import com.example.user_authentication.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private var backPressTime : Long = 0 // to count back button double click
    private lateinit var callback : OnBackPressedCallback
    private lateinit var binding : ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
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


        // get user name from loginActivity
        val userName = intent.getStringExtra("username").toString()
        binding.userName.text = userName

        binding.logout.setOnClickListener {
            val intent = Intent(this@MainActivity, LoginActivity::class.java)
            // put logout info
            intent.putExtra("logout",true)
            startActivity(intent)
        }
    }
}
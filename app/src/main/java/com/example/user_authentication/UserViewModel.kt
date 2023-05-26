package com.example.user_authentication

import androidx.lifecycle.ViewModel

class UserViewModel :ViewModel(){
    var userName : String? = null

    fun saveUser(name:String){
        userName = name
    }

    fun removeUser(){
        userName = null
    }
}
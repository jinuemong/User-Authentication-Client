package com.example.user_authentication.Server

import okhttp3.ResponseBody

// Resource : To distinguish whether
// communication is successful or not
// case 1 -> Success
// case 2 -> Failure

sealed class Resource<out T> {
    // case 1
    data class Success<out T>(val value : T) : Resource<T>()

    // case 2
    data class Failure(
        val isNetworkError : Boolean,
        val errorCod : Int?,
        val errorBody : ResponseBody?
    ) : Resource<Nothing>()
}
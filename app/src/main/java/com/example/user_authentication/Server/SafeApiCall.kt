package com.example.user_authentication.Server

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException

// for safe API transmission
// purpose : Resource.Success(apiCall.invoke())
// if fail -> return error

interface SafeApiCall {

    suspend fun <T> safeApiCall(
        apiCall: suspend  () -> T
    ): Resource<T>{
        return withContext(Dispatchers.IO){
            try {

                // if Success
                Resource.Success(apiCall.invoke())
            } catch (t : Throwable){
                // is Fail
                when (t){
                    // case -> http error
                    is HttpException ->{
                        Resource.Failure(false,t.code(),
                        t.response()?.errorBody())
                    }
                    // case -> other
                    else ->{
                        Resource.Failure(true,null,null)
                    }
                }
            }
        }
    }
}
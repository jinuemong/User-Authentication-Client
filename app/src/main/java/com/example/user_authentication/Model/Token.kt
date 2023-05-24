package com.example.user_authentication.Model

import java.io.Serializable

class Token (
    val access:String,
    val refresh:String,
): Serializable
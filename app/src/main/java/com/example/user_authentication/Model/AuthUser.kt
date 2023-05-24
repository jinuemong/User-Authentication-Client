package com.example.user_authentication.Model

import java.io.Serializable

class AuthUser(
    val user : User,
    val token : Token
) : Serializable
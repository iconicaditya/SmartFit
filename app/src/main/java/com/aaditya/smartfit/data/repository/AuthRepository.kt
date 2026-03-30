package com.aaditya.smartfit.data.repository

import com.aaditya.smartfit.data.repository.model.AuthResult
import com.aaditya.smartfit.data.repository.model.RegisterResult

interface AuthRepository {
    suspend fun login(email: String, password: String): AuthResult
    suspend fun register(name: String, email: String, password: String): RegisterResult
    suspend fun logout()

    suspend fun isLoggedIn(): Boolean
    suspend fun currentUserId(): Long?
}


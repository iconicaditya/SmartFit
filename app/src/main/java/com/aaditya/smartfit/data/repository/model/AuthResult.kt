package com.aaditya.smartfit.data.repository.model

sealed interface AuthResult {
    data class Success(
        val userId: Long
    ) : AuthResult

    data object InvalidCredentials : AuthResult
}


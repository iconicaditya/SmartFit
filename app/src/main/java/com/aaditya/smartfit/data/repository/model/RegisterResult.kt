package com.aaditya.smartfit.data.repository.model

sealed interface RegisterResult {
    data class Success(
        val userId: Long
    ) : RegisterResult

    data object EmailAlreadyExists : RegisterResult
}


package com.aaditya.smartfit.feature.auth.login

data class LoginUiState(
    val email: String = "",
    val password: String = "",
    val passwordVisible: Boolean = false,
    val emailError: String? = null,
    val passwordError: String? = null,
    val globalMessage: String? = null,
    val globalMessageSuccess: Boolean = false,
    val isLoading: Boolean = false
)

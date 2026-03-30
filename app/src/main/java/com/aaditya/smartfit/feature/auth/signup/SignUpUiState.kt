package com.aaditya.smartfit.feature.auth.signup

data class SignUpUiState(
    val fullName: String = "",
    val email: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val passwordVisible: Boolean = false,
    val confirmPasswordVisible: Boolean = false,
    val nameError: String? = null,
    val emailError: String? = null,
    val passwordError: String? = null,
    val confirmPasswordError: String? = null,
    val globalMessage: String? = null,
    val globalMessageSuccess: Boolean = false,
    val isLoading: Boolean = false
)

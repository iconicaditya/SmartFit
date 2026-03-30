package com.aaditya.smartfit.feature.auth.signup

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aaditya.smartfit.data.repository.AuthRepository
import com.aaditya.smartfit.data.repository.model.RegisterResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SignUpViewModel(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SignUpUiState())
    val uiState: StateFlow<SignUpUiState> = _uiState.asStateFlow()

    fun onEvent(event: SignUpUiEvent) {
        when (event) {
            is SignUpUiEvent.NameChanged -> {
                _uiState.update { it.copy(fullName = event.value, nameError = null, globalMessage = null) }
            }

            is SignUpUiEvent.EmailChanged -> {
                _uiState.update { it.copy(email = event.value, emailError = null, globalMessage = null) }
            }

            is SignUpUiEvent.PasswordChanged -> {
                _uiState.update {
                    it.copy(
                        password = event.value,
                        passwordError = null,
                        confirmPasswordError = if (
                            it.confirmPassword.isNotBlank() && it.confirmPassword != event.value
                        ) {
                            "Passwords do not match."
                        } else {
                            null
                        },
                        globalMessage = null
                    )
                }
            }

            is SignUpUiEvent.ConfirmPasswordChanged -> {
                _uiState.update {
                    it.copy(
                        confirmPassword = event.value,
                        confirmPasswordError = null,
                        globalMessage = null
                    )
                }
            }

            SignUpUiEvent.TogglePasswordVisibility -> {
                _uiState.update { state -> state.copy(passwordVisible = !state.passwordVisible) }
            }

            SignUpUiEvent.ToggleConfirmPasswordVisibility -> {
                _uiState.update { state ->
                    state.copy(confirmPasswordVisible = !state.confirmPasswordVisible)
                }
            }

            SignUpUiEvent.Submit -> submit()

            SignUpUiEvent.MessageDismissed -> {
                _uiState.update { it.copy(globalMessage = null) }
            }
        }
    }

    private fun submit() {
        val state = _uiState.value

        val name = state.fullName.trim()
        val email = state.email.trim()
        val password = state.password
        val confirmPassword = state.confirmPassword

        val nameError = if (name.isBlank()) "Full name cannot be empty." else null
        val emailError = if (isValidEmail(email)) null else "Please enter a valid email address."
        val passwordError = if (password.length < 6) "Password must be at least 6 characters." else null
        val confirmPasswordError = when {
            confirmPassword.isBlank() -> "Please confirm your password."
            confirmPassword != password -> "Passwords do not match."
            else -> null
        }

        if (nameError != null || emailError != null || passwordError != null || confirmPasswordError != null) {
            _uiState.update {
                it.copy(
                    nameError = nameError,
                    emailError = emailError,
                    passwordError = passwordError,
                    confirmPasswordError = confirmPasswordError,
                    globalMessage = null,
                    globalMessageSuccess = false,
                    isLoading = false
                )
            }
            return
        }

        _uiState.update { it.copy(isLoading = true, globalMessage = null, globalMessageSuccess = false) }

        viewModelScope.launch {
            when (authRepository.register(name = name, email = email, password = password)) {
                is RegisterResult.Success -> {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            globalMessage = "Account created successfully! You can now log in.",
                            globalMessageSuccess = true
                        )
                    }
                }

                RegisterResult.EmailAlreadyExists -> {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            globalMessage = "Email already exists.",
                            globalMessageSuccess = false
                        )
                    }
                }
            }
        }
    }

    private fun isValidEmail(input: String): Boolean {
        if (input.isBlank()) return false
        val atIndex = input.indexOf('@')
        return atIndex > 0 && atIndex < input.lastIndex && input.contains('.')
    }
}


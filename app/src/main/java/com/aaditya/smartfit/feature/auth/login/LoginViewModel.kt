package com.aaditya.smartfit.feature.auth.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aaditya.smartfit.data.repository.AuthRepository
import com.aaditya.smartfit.data.repository.model.AuthResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class LoginViewModel(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    fun onEvent(event: LoginUiEvent, onLoginSuccess: () -> Unit = {}) {
        when (event) {
            is LoginUiEvent.EmailChanged -> {
                _uiState.update {
                    it.copy(
                        email = event.value,
                        emailError = null,
                        globalMessage = null,
                        globalMessageSuccess = false
                    )
                }
            }

            is LoginUiEvent.PasswordChanged -> {
                _uiState.update {
                    it.copy(
                        password = event.value,
                        passwordError = null,
                        globalMessage = null,
                        globalMessageSuccess = false
                    )
                }
            }

            LoginUiEvent.TogglePasswordVisibility -> {
                _uiState.update { state -> state.copy(passwordVisible = !state.passwordVisible) }
            }

            LoginUiEvent.Submit -> submit(onLoginSuccess)

            LoginUiEvent.MessageDismissed -> {
                _uiState.update { it.copy(globalMessage = null) }
            }
        }
    }

    private fun submit(onLoginSuccess: () -> Unit) {
        val state = _uiState.value
        val email = state.email.trim()
        val password = state.password

        val emailError = if (isValidEmail(email)) null else "Please enter a valid email address."
        val passwordError = if (password.isBlank()) null else null

        if (emailError != null || passwordError != null || password.isBlank()) {
            _uiState.update {
                it.copy(
                    emailError = emailError,
                    passwordError = if (password.isBlank()) "Password cannot be empty." else passwordError,
                    globalMessage = null,
                    globalMessageSuccess = false,
                    isLoading = false
                )
            }
            return
        }

        _uiState.update { it.copy(isLoading = true, globalMessage = null, globalMessageSuccess = false) }

        viewModelScope.launch {
            when (authRepository.login(email = email, password = password)) {
                is AuthResult.Success -> {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            globalMessage = "Login successful! Ready to continue.",
                            globalMessageSuccess = true
                        )
                    }
                    onLoginSuccess()
                }

                AuthResult.InvalidCredentials -> {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            globalMessage = "Invalid email or password.",
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


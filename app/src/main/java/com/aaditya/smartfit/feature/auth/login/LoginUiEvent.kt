package com.aaditya.smartfit.feature.auth.login

sealed interface LoginUiEvent {
    data class EmailChanged(
        val value: String
    ) : LoginUiEvent

    data class PasswordChanged(
        val value: String
    ) : LoginUiEvent

    data object TogglePasswordVisibility : LoginUiEvent
    data object Submit : LoginUiEvent
    data object MessageDismissed : LoginUiEvent
}

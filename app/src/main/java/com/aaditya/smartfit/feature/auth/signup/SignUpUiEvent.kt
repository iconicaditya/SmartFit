package com.aaditya.smartfit.feature.auth.signup

sealed interface SignUpUiEvent {
    data class NameChanged(
        val value: String
    ) : SignUpUiEvent

    data class EmailChanged(
        val value: String
    ) : SignUpUiEvent

    data class PasswordChanged(
        val value: String
    ) : SignUpUiEvent

    data class ConfirmPasswordChanged(
        val value: String
    ) : SignUpUiEvent

    data object TogglePasswordVisibility : SignUpUiEvent
    data object ToggleConfirmPasswordVisibility : SignUpUiEvent
    data object Submit : SignUpUiEvent
    data object MessageDismissed : SignUpUiEvent
}

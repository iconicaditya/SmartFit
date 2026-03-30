package com.aaditya.smartfit.feature.profile

import android.net.Uri

sealed interface ProfileUiEvent {
    data object BackClicked : ProfileUiEvent
    data object AvatarClicked : ProfileUiEvent
    data class AvatarSelected(val uri: Uri?) : ProfileUiEvent
    data class NameChanged(val value: String) : ProfileUiEvent
    data class EmailChanged(val value: String) : ProfileUiEvent
    data class DarkModeToggled(val enabled: Boolean) : ProfileUiEvent
    data class NotificationsToggled(val enabled: Boolean) : ProfileUiEvent
    data class UnitPreferenceChanged(val unitPreference: UnitPreference) : ProfileUiEvent
    data class StepGoalInputChanged(val value: String) : ProfileUiEvent
    data class WorkoutGoalInputChanged(val value: String) : ProfileUiEvent
    data class CalorieGoalInputChanged(val value: String) : ProfileUiEvent
    data object SaveClicked : ProfileUiEvent
    data object LogoutClicked : ProfileUiEvent
    data object MessageDismissed : ProfileUiEvent
}

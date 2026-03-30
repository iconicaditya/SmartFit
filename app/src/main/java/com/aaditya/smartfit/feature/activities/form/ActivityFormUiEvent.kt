package com.aaditya.smartfit.feature.activities.form

import com.aaditya.smartfit.feature.activities.list.ActivityType

sealed interface ActivityFormUiEvent {
    data class OnNameChanged(
        val value: String
    ) : ActivityFormUiEvent

    data class OnTypeChanged(
        val value: ActivityType
    ) : ActivityFormUiEvent

    data class OnDurationChanged(
        val value: String
    ) : ActivityFormUiEvent

    data class OnCaloriesChanged(
        val value: String
    ) : ActivityFormUiEvent

    data class OnDateChanged(
        val value: String
    ) : ActivityFormUiEvent

    data class OnTimeChanged(
        val value: String
    ) : ActivityFormUiEvent

    data object OnSubmitClicked : ActivityFormUiEvent

    data object OnMessageDismissed : ActivityFormUiEvent

    data object OnBackClicked : ActivityFormUiEvent
}

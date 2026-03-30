package com.aaditya.smartfit.feature.activities.form

import com.aaditya.smartfit.feature.activities.list.ActivityType

data class ActivityFormUiState(
    val activityId: String? = null,
    val isEditMode: Boolean = false,
    val activityName: String = "",
    val activityType: ActivityType = ActivityType.WALKING,
    val durationMinutes: String = "",
    val calories: String = "",
    val dateLabel: String = "",
    val timeLabel: String = "",
    val nameError: String? = null,
    val durationError: String? = null,
    val caloriesError: String? = null,
    val dateError: String? = null,
    val timeError: String? = null,
    val isSaving: Boolean = false,
    val globalMessage: String? = null,
    val isSuccessMessage: Boolean = false
)

package com.aaditya.smartfit.feature.profile

import android.net.Uri

const val PROFILE_STEP_GOAL_MIN = 1000
const val PROFILE_STEP_GOAL_MAX = 50000
const val PROFILE_STEP_GOAL_DEFAULT = 10000

const val PROFILE_WORKOUT_GOAL_MIN = 10
const val PROFILE_WORKOUT_GOAL_MAX = 180
const val PROFILE_WORKOUT_GOAL_DEFAULT = 30

const val PROFILE_CALORIE_GOAL_MIN = 1000
const val PROFILE_CALORIE_GOAL_MAX = 5000
const val PROFILE_CALORIE_GOAL_DEFAULT = 2000

data class ProfileUiState(
    val name: String = "Aaditya Chaudhary",
    val email: String = "aaditya@smartfit.app",
    val isDarkMode: Boolean = false,
    val notificationsEnabled: Boolean = true,
    val unitPreference: UnitPreference = UnitPreference.STEPS,
    val profileImageUri: Uri? = null,
    val stepGoalInput: String = PROFILE_STEP_GOAL_DEFAULT.toString(),
    val workoutGoalInput: String = PROFILE_WORKOUT_GOAL_DEFAULT.toString(),
    val calorieGoalInput: String = PROFILE_CALORIE_GOAL_DEFAULT.toString(),
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val errorMessage: String? = null,
    val successMessage: String? = null
)

enum class UnitPreference {
    STEPS,
    KILOMETERS
}

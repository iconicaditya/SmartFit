package com.aaditya.smartfit.feature.activities.list

enum class ActivitiesTab {
    ACTIVITIES,
    FOOD
}

enum class ActivityFilterOption {
    TODAY,
    WEEKLY,
    ALL_TIME
}

enum class ActivitySortOption {
    NEWEST,
    CALORIES_HIGH,
    DURATION_LONG
}

enum class ActivityType {
    WALKING,
    RUNNING,
    CYCLING,
    STRENGTH,
    YOGA
}

data class ActivityUiModel(
    val id: String,
    val name: String,
    val type: ActivityType,
    val durationMinutes: Int,
    val calories: Int,
    val timestampMillis: Long
)

enum class MealType {
    BREAKFAST,
    LUNCH,
    DINNER,
    SNACKS
}

enum class FoodFilterOption {
    ALL_MEALS,
    BREAKFAST,
    LUNCH,
    DINNER,
    SNACKS
}

enum class FoodSortOption {
    NEWEST,
    CALORIES_HIGH,
    CALORIES_LOW
}

data class FoodUiModel(
    val id: String,
    val name: String,
    val calories: Int,
    val mealType: MealType,
    val consumedAtMillis: Long,
    val notes: String? = null
)

sealed interface ActivitiesUiState {
    data object Loading : ActivitiesUiState

    data object Empty : ActivitiesUiState

    data class Error(
        val message: String
    ) : ActivitiesUiState

    data class Success(
        val activities: List<ActivityUiModel>,
        val foods: List<FoodUiModel> = emptyList()
    ) : ActivitiesUiState
}

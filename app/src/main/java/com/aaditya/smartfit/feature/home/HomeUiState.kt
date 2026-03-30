package com.aaditya.smartfit.feature.home

import com.aaditya.smartfit.feature.profile.PROFILE_CALORIE_GOAL_DEFAULT
import com.aaditya.smartfit.feature.profile.PROFILE_STEP_GOAL_DEFAULT
import com.aaditya.smartfit.feature.profile.PROFILE_WORKOUT_GOAL_DEFAULT

data class HomeUiState(
    val isLoading: Boolean = true,
    val greetingName: String = "User",
    val stepsToday: Int = 0,
    val caloriesIntakeToday: Int = 0,
    val caloriesBurnedToday: Int = 0,
    val workoutsCount: Int = 0,
    val activeMinutesToday: Int = 0,
    val stepGoal: Int = PROFILE_STEP_GOAL_DEFAULT,
    val workoutGoalMinutes: Int = PROFILE_WORKOUT_GOAL_DEFAULT,
    val calorieGoal: Int = PROFILE_CALORIE_GOAL_DEFAULT,
    val recentActivities: List<HomeRecentActivity> = emptyList()
)

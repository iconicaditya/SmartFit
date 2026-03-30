package com.aaditya.smartfit.data.repository

import com.aaditya.smartfit.feature.profile.UnitPreference
import kotlinx.coroutines.flow.Flow

data class ProfileData(
    val userId: Long,
    val name: String,
    val email: String,
    val isDarkMode: Boolean,
    val notificationsEnabled: Boolean,
    val unitPreference: UnitPreference,
    val stepGoal: Int,
    val workoutGoal: Int,
    val calorieGoal: Int
)

data class ProfileUpdateRequest(
    val name: String,
    val email: String,
    val isDarkMode: Boolean,
    val notificationsEnabled: Boolean,
    val unitPreference: UnitPreference,
    val stepGoal: Int,
    val workoutGoal: Int,
    val calorieGoal: Int
)

interface ProfileRepository {
    fun profileFlow(userId: Long): Flow<ProfileData>

    suspend fun updateProfile(userId: Long, request: ProfileUpdateRequest)
    suspend fun setThemeDark(enabled: Boolean)
}


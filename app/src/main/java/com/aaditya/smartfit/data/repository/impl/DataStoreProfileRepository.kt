package com.aaditya.smartfit.data.repository.impl

import com.aaditya.smartfit.data.local.dao.UserDao
import com.aaditya.smartfit.data.preferences.UserPreferencesDataStore
import com.aaditya.smartfit.data.repository.ProfileData
import com.aaditya.smartfit.data.repository.ProfileRepository
import com.aaditya.smartfit.data.repository.ProfileUpdateRequest
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine

class DataStoreProfileRepository(
    private val userDao: UserDao,
    private val preferencesDataStore: UserPreferencesDataStore
) : ProfileRepository {

    override fun profileFlow(userId: Long): Flow<ProfileData> {
        return combine(
            userDao.observeById(userId),
            preferencesDataStore.preferencesFlow
        ) { user, preferences ->
            val resolvedUser = user
                ?: throw IllegalStateException("User not found for id=$userId")

            ProfileData(
                userId = resolvedUser.id,
                name = resolvedUser.fullName,
                email = resolvedUser.email,
                isDarkMode = preferences.darkMode,
                notificationsEnabled = preferences.notificationsEnabled,
                unitPreference = preferences.unitPreference,
                stepGoal = preferences.stepGoal,
                workoutGoal = preferences.workoutGoal,
                calorieGoal = preferences.calorieGoal
            )
        }
    }

    override suspend fun updateProfile(userId: Long, request: ProfileUpdateRequest) {
        val current = userDao.findById(userId)
            ?: throw IllegalStateException("User not found for id=$userId")

        userDao.update(
            current.copy(
                fullName = request.name.trim(),
                email = request.email.trim().lowercase(),
                updatedAtMillis = System.currentTimeMillis()
            )
        )

        preferencesDataStore.setDarkMode(request.isDarkMode)
        preferencesDataStore.setNotificationsEnabled(request.notificationsEnabled)
        preferencesDataStore.setUnitPreference(request.unitPreference)
        preferencesDataStore.setStepGoal(request.stepGoal)
        preferencesDataStore.setWorkoutGoal(request.workoutGoal)
        preferencesDataStore.setCalorieGoal(request.calorieGoal)
    }

    override suspend fun setThemeDark(enabled: Boolean) {
        preferencesDataStore.setDarkMode(enabled)
    }
}


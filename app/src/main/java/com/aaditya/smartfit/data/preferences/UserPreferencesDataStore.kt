package com.aaditya.smartfit.data.preferences

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.aaditya.smartfit.feature.profile.PROFILE_CALORIE_GOAL_DEFAULT
import com.aaditya.smartfit.feature.profile.PROFILE_STEP_GOAL_DEFAULT
import com.aaditya.smartfit.feature.profile.PROFILE_WORKOUT_GOAL_DEFAULT
import com.aaditya.smartfit.feature.profile.UnitPreference
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private const val SMARTFIT_PREFERENCES = "smartfit_preferences"

private val Context.dataStore by preferencesDataStore(name = SMARTFIT_PREFERENCES)

data class UserPreferences(
    val darkMode: Boolean = false,
    val notificationsEnabled: Boolean = true,
    val unitPreference: UnitPreference = UnitPreference.STEPS,
    val stepGoal: Int = PROFILE_STEP_GOAL_DEFAULT,
    val workoutGoal: Int = PROFILE_WORKOUT_GOAL_DEFAULT,
    val calorieGoal: Int = PROFILE_CALORIE_GOAL_DEFAULT,
    val activeUserId: Long? = null
)

class UserPreferencesDataStore(
    context: Context
) {
    private val dataStore = context.dataStore

    private object Keys {
        val DARK_MODE: Preferences.Key<Boolean> = booleanPreferencesKey("dark_mode")
        val NOTIFICATIONS: Preferences.Key<Boolean> = booleanPreferencesKey("notifications_enabled")
        val UNIT: Preferences.Key<String> = stringPreferencesKey("unit_preference")
        val STEP_GOAL: Preferences.Key<Int> = intPreferencesKey("step_goal")
        val WORKOUT_GOAL: Preferences.Key<Int> = intPreferencesKey("workout_goal")
        val CALORIE_GOAL: Preferences.Key<Int> = intPreferencesKey("calorie_goal")
        val ACTIVE_USER_ID: Preferences.Key<Long> = longPreferencesKey("active_user_id")
    }

    val preferencesFlow: Flow<UserPreferences> = dataStore.data.map { prefs ->
        UserPreferences(
            darkMode = prefs[Keys.DARK_MODE] ?: false,
            notificationsEnabled = prefs[Keys.NOTIFICATIONS] ?: true,
            unitPreference = prefs[Keys.UNIT]
                ?.let { raw ->
                    runCatching { UnitPreference.valueOf(raw) }
                        .getOrDefault(UnitPreference.STEPS)
                }
                ?: UnitPreference.STEPS,
            stepGoal = prefs[Keys.STEP_GOAL] ?: PROFILE_STEP_GOAL_DEFAULT,
            workoutGoal = prefs[Keys.WORKOUT_GOAL] ?: PROFILE_WORKOUT_GOAL_DEFAULT,
            calorieGoal = prefs[Keys.CALORIE_GOAL] ?: PROFILE_CALORIE_GOAL_DEFAULT,
            activeUserId = prefs[Keys.ACTIVE_USER_ID]
        )
    }

    suspend fun setDarkMode(enabled: Boolean) {
        dataStore.edit { prefs ->
            prefs[Keys.DARK_MODE] = enabled
        }
    }

    suspend fun setNotificationsEnabled(enabled: Boolean) {
        dataStore.edit { prefs ->
            prefs[Keys.NOTIFICATIONS] = enabled
        }
    }

    suspend fun setUnitPreference(unitPreference: UnitPreference) {
        dataStore.edit { prefs ->
            prefs[Keys.UNIT] = unitPreference.name
        }
    }

    suspend fun setStepGoal(value: Int) {
        dataStore.edit { prefs ->
            prefs[Keys.STEP_GOAL] = value
        }
    }

    suspend fun setWorkoutGoal(value: Int) {
        dataStore.edit { prefs ->
            prefs[Keys.WORKOUT_GOAL] = value
        }
    }

    suspend fun setCalorieGoal(value: Int) {
        dataStore.edit { prefs ->
            prefs[Keys.CALORIE_GOAL] = value
        }
    }

    suspend fun setActiveUser(userId: Long) {
        dataStore.edit { prefs ->
            prefs[Keys.ACTIVE_USER_ID] = userId
        }
    }

    suspend fun clearActiveUser() {
        dataStore.edit { prefs ->
            prefs.remove(Keys.ACTIVE_USER_ID)
        }
    }
}


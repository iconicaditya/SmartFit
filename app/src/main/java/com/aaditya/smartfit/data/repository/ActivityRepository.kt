package com.aaditya.smartfit.data.repository

import com.aaditya.smartfit.feature.activities.list.ActivityType
import com.aaditya.smartfit.feature.activities.list.ActivityUiModel
import com.aaditya.smartfit.feature.activities.list.FoodUiModel
import com.aaditya.smartfit.feature.activities.list.MealType
import kotlinx.coroutines.flow.Flow

interface ActivityRepository {
    val activitiesFlow: Flow<List<ActivityUiModel>>
    val foodsFlow: Flow<List<FoodUiModel>>

    suspend fun ensureSeedData()

    suspend fun getActivityById(id: String): ActivityUiModel?
    suspend fun getFoodById(id: String): FoodUiModel?

    suspend fun addActivity(
        name: String,
        type: ActivityType,
        durationMinutes: Int,
        calories: Int,
        timestampMillis: Long
    )

    suspend fun updateActivity(
        id: String,
        name: String,
        type: ActivityType,
        durationMinutes: Int,
        calories: Int,
        timestampMillis: Long
    )

    suspend fun deleteActivity(id: String)
    suspend fun upsertActivity(item: ActivityUiModel)

    suspend fun addFood(
        name: String,
        calories: Int,
        mealType: MealType,
        consumedAtMillis: Long,
        notes: String?
    )

    suspend fun updateFood(
        id: String,
        name: String,
        calories: Int,
        mealType: MealType,
        consumedAtMillis: Long,
        notes: String?
    )

    suspend fun deleteFood(id: String)
    suspend fun upsertFood(item: FoodUiModel)
}


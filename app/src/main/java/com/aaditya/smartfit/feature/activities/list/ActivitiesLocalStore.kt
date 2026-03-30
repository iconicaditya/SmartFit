package com.aaditya.smartfit.feature.activities.list

import androidx.compose.runtime.mutableStateListOf
import kotlin.math.max

object ActivitiesLocalStore {
    private val activityItems = mutableStateListOf<ActivityUiModel>()
    private val foodItems = mutableStateListOf<FoodUiModel>()

    init {
        if (activityItems.isEmpty()) {
            activityItems.addAll(ActivitiesMockData.activities)
        }
        if (foodItems.isEmpty()) {
            foodItems.addAll(ActivitiesMockData.foods)
        }
    }

    val activities: List<ActivityUiModel>
        get() = activityItems

    val foods: List<FoodUiModel>
        get() = foodItems

    fun getById(id: String): ActivityUiModel? {
        return activityItems.firstOrNull { it.id == id }
    }

    fun getFoodById(id: String): FoodUiModel? {
        return foodItems.firstOrNull { it.id == id }
    }

    fun addActivity(
        name: String,
        type: ActivityType,
        durationMinutes: Int,
        calories: Int,
        timestampMillis: Long
    ) {
        val newId = generateId()
        activityItems.add(
            index = 0,
            element = ActivityUiModel(
                id = newId,
                name = name,
                type = type,
                durationMinutes = durationMinutes,
                calories = calories,
                timestampMillis = timestampMillis
            )
        )
    }

    fun updateActivity(
        id: String,
        name: String,
        type: ActivityType,
        durationMinutes: Int,
        calories: Int,
        timestampMillis: Long
    ) {
        val index = activityItems.indexOfFirst { it.id == id }
        if (index < 0) return

        activityItems[index] = activityItems[index].copy(
            name = name,
            type = type,
            durationMinutes = durationMinutes,
            calories = calories,
            timestampMillis = timestampMillis
        )
    }

    fun deleteActivity(id: String) {
        activityItems.removeAll { it.id == id }
    }

    fun restoreActivity(item: ActivityUiModel) {
        activityItems.add(index = 0, element = item)
    }

    fun addFood(
        name: String,
        calories: Int,
        mealType: MealType,
        consumedAtMillis: Long,
        notes: String?
    ) {
        val newId = generateFoodId()
        foodItems.add(
            index = 0,
            element = FoodUiModel(
                id = newId,
                name = name,
                calories = calories,
                mealType = mealType,
                consumedAtMillis = consumedAtMillis,
                notes = notes
            )
        )
    }

    fun updateFood(
        id: String,
        name: String,
        calories: Int,
        mealType: MealType,
        consumedAtMillis: Long,
        notes: String?
    ) {
        val index = foodItems.indexOfFirst { it.id == id }
        if (index < 0) return

        foodItems[index] = foodItems[index].copy(
            name = name,
            calories = calories,
            mealType = mealType,
            consumedAtMillis = consumedAtMillis,
            notes = notes
        )
    }

    fun deleteFood(id: String) {
        foodItems.removeAll { it.id == id }
    }

    fun restoreFood(item: FoodUiModel) {
        foodItems.add(index = 0, element = item)
    }

    private fun generateId(): String {
        val maxNumeric = activityItems
            .mapNotNull { model ->
                model.id
                    .removePrefix("a_")
                    .toIntOrNull()
            }
            .fold(0, ::max)

        return "a_${maxNumeric + 1}"
    }

    private fun generateFoodId(): String {
        val maxNumeric = foodItems
            .mapNotNull { model ->
                model.id
                    .removePrefix("f_")
                    .toIntOrNull()
            }
            .fold(0, ::max)

        return "f_${maxNumeric + 1}"
    }
}

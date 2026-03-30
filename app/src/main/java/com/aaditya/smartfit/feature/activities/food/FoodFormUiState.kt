package com.aaditya.smartfit.feature.activities.food

import com.aaditya.smartfit.feature.activities.list.MealType

data class FoodFormUiState(
    val foodId: String? = null,
    val isEditMode: Boolean = false,
    val foodName: String = "",
    val calories: String = "",
    val mealType: MealType = MealType.BREAKFAST,
    val timeLabel: String = "",
    val notes: String = "",
    val foodNameError: String? = null,
    val caloriesError: String? = null,
    val timeError: String? = null,
    val isSaving: Boolean = false,
    val globalMessage: String? = null,
    val isSuccessMessage: Boolean = false
)


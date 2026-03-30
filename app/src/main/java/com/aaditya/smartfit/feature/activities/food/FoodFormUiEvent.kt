package com.aaditya.smartfit.feature.activities.food

import com.aaditya.smartfit.feature.activities.list.MealType

sealed interface FoodFormUiEvent {
    data class OnFoodNameChanged(
        val value: String
    ) : FoodFormUiEvent

    data class OnCaloriesChanged(
        val value: String
    ) : FoodFormUiEvent

    data class OnMealTypeChanged(
        val value: MealType
    ) : FoodFormUiEvent

    data class OnTimeChanged(
        val value: String
    ) : FoodFormUiEvent

    data class OnNotesChanged(
        val value: String
    ) : FoodFormUiEvent

    data object OnSubmitClicked : FoodFormUiEvent

    data object OnMessageDismissed : FoodFormUiEvent

    data object OnBackClicked : FoodFormUiEvent
}


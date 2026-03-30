package com.aaditya.smartfit.feature.activities.food

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aaditya.smartfit.data.repository.ActivityRepository
import com.aaditya.smartfit.feature.activities.list.MealType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class FoodFormViewModel(
    private val activityRepository: ActivityRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(FoodFormUiState())
    val uiState: StateFlow<FoodFormUiState> = _uiState.asStateFlow()

    fun load(foodId: String?) {
        if (foodId == null) {
            _uiState.value = FoodFormUiState(foodId = null, isEditMode = false)
            return
        }

        viewModelScope.launch {
            val existing = activityRepository.getFoodById(foodId)
            if (existing == null) {
                _uiState.value = FoodFormUiState(foodId = foodId, isEditMode = false)
                return@launch
            }

            _uiState.value = FoodFormUiState(
                foodId = existing.id,
                isEditMode = true,
                foodName = existing.name,
                calories = existing.calories.toString(),
                mealType = existing.mealType,
                timeLabel = formatTime(existing.consumedAtMillis),
                notes = existing.notes.orEmpty()
            )
        }
    }

    fun onEvent(event: FoodFormUiEvent, onSaved: () -> Unit = {}) {
        when (event) {
            is FoodFormUiEvent.OnFoodNameChanged -> {
                _uiState.update {
                    it.copy(
                        foodName = event.value,
                        foodNameError = null,
                        globalMessage = null
                    )
                }
            }

            is FoodFormUiEvent.OnCaloriesChanged -> {
                _uiState.update {
                    it.copy(
                        calories = event.value.filter(Char::isDigit),
                        caloriesError = null,
                        globalMessage = null
                    )
                }
            }

            is FoodFormUiEvent.OnMealTypeChanged -> {
                _uiState.update { it.copy(mealType = event.value) }
            }

            is FoodFormUiEvent.OnTimeChanged -> {
                _uiState.update { it.copy(timeLabel = event.value, timeError = null, globalMessage = null) }
            }

            is FoodFormUiEvent.OnNotesChanged -> {
                _uiState.update { it.copy(notes = event.value, globalMessage = null) }
            }

            FoodFormUiEvent.OnSubmitClicked -> submit(onSaved)

            FoodFormUiEvent.OnMessageDismissed -> {
                _uiState.update { it.copy(globalMessage = null) }
            }

            FoodFormUiEvent.OnBackClicked -> Unit
        }
    }

    private fun submit(onSaved: () -> Unit) {
        val state = _uiState.value

        val nameError = if (state.foodName.trim().length < 2) {
            "Food name must be at least 2 characters."
        } else {
            null
        }

        val caloriesValue = state.calories.toIntOrNull()
        val caloriesError = if (caloriesValue == null || caloriesValue <= 0) {
            "Enter valid calories greater than 0."
        } else {
            null
        }

        val timeError = if (state.timeLabel.isBlank()) "Time is required." else null
        val notesError = if (state.notes.length > 150) "Notes should be 150 characters or less." else null

        if (nameError != null || caloriesError != null || timeError != null || notesError != null) {
            _uiState.update {
                it.copy(
                    foodNameError = nameError,
                    caloriesError = caloriesError,
                    timeError = timeError,
                    isSaving = false,
                    globalMessage = notesError,
                    isSuccessMessage = false
                )
            }
            return
        }

        _uiState.update { it.copy(isSaving = true, globalMessage = null, isSuccessMessage = false) }

        viewModelScope.launch {
            val timestamp = System.currentTimeMillis()
            val notesValue = state.notes.trim().ifBlank { null }

            if (state.isEditMode && state.foodId != null) {
                activityRepository.updateFood(
                    id = state.foodId,
                    name = state.foodName.trim(),
                    calories = caloriesValue ?: 0,
                    mealType = state.mealType,
                    consumedAtMillis = timestamp,
                    notes = notesValue
                )

                _uiState.update {
                    it.copy(
                        isSaving = false,
                        globalMessage = "Food entry updated successfully",
                        isSuccessMessage = true
                    )
                }
            } else {
                activityRepository.addFood(
                    name = state.foodName.trim(),
                    calories = caloriesValue ?: 0,
                    mealType = state.mealType,
                    consumedAtMillis = timestamp,
                    notes = notesValue
                )

                _uiState.update {
                    it.copy(
                        isSaving = false,
                        globalMessage = "Food entry added successfully",
                        isSuccessMessage = true
                    )
                }
            }

            onSaved()
        }
    }

    private fun formatTime(timestampMillis: Long): String {
        return SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date(timestampMillis))
    }
}


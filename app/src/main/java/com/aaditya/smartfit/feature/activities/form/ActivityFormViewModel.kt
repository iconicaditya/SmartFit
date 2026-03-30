package com.aaditya.smartfit.feature.activities.form

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aaditya.smartfit.data.repository.ActivityRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ActivityFormViewModel(
    private val activityRepository: ActivityRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ActivityFormUiState())
    val uiState: StateFlow<ActivityFormUiState> = _uiState.asStateFlow()

    fun load(activityId: String?) {
        if (activityId == null) {
            _uiState.value = ActivityFormUiState(activityId = null, isEditMode = false)
            return
        }

        viewModelScope.launch {
            val existing = activityRepository.getActivityById(activityId)
            if (existing == null) {
                _uiState.value = ActivityFormUiState(activityId = activityId, isEditMode = false)
                return@launch
            }

            _uiState.value = ActivityFormUiState(
                activityId = existing.id,
                isEditMode = true,
                activityName = existing.name,
                activityType = existing.type,
                durationMinutes = existing.durationMinutes.toString(),
                calories = existing.calories.toString(),
                dateLabel = formatDate(existing.timestampMillis),
                timeLabel = formatTime(existing.timestampMillis)
            )
        }
    }

    fun onEvent(event: ActivityFormUiEvent, onSaved: () -> Unit = {}) {
        when (event) {
            is ActivityFormUiEvent.OnNameChanged -> {
                _uiState.update { it.copy(activityName = event.value, nameError = null, globalMessage = null) }
            }

            is ActivityFormUiEvent.OnTypeChanged -> {
                _uiState.update { it.copy(activityType = event.value) }
            }

            is ActivityFormUiEvent.OnDurationChanged -> {
                _uiState.update {
                    it.copy(
                        durationMinutes = event.value.filter(Char::isDigit),
                        durationError = null,
                        globalMessage = null
                    )
                }
            }

            is ActivityFormUiEvent.OnCaloriesChanged -> {
                _uiState.update {
                    it.copy(
                        calories = event.value.filter(Char::isDigit),
                        caloriesError = null,
                        globalMessage = null
                    )
                }
            }

            is ActivityFormUiEvent.OnDateChanged -> {
                _uiState.update { it.copy(dateLabel = event.value, dateError = null, globalMessage = null) }
            }

            is ActivityFormUiEvent.OnTimeChanged -> {
                _uiState.update { it.copy(timeLabel = event.value, timeError = null, globalMessage = null) }
            }

            ActivityFormUiEvent.OnSubmitClicked -> {
                submit(onSaved)
            }

            ActivityFormUiEvent.OnMessageDismissed -> {
                _uiState.update { it.copy(globalMessage = null) }
            }

            ActivityFormUiEvent.OnBackClicked -> Unit
        }
    }

    private fun submit(onSaved: () -> Unit) {
        val state = _uiState.value

        val nameError = if (state.activityName.isBlank()) "Activity name is required" else null
        val durationValue = state.durationMinutes.toIntOrNull()
        val durationError = if (durationValue == null || durationValue <= 0) "Enter valid duration" else null
        val caloriesValue = state.calories.toIntOrNull()
        val caloriesError = if (caloriesValue == null || caloriesValue <= 0) "Enter valid calories" else null
        val dateError = if (state.dateLabel.isBlank()) "Date is required" else null
        val timeError = if (state.timeLabel.isBlank()) "Time is required" else null

        if (nameError != null || durationError != null || caloriesError != null || dateError != null || timeError != null) {
            _uiState.update {
                it.copy(
                    nameError = nameError,
                    durationError = durationError,
                    caloriesError = caloriesError,
                    dateError = dateError,
                    timeError = timeError,
                    isSaving = false,
                    globalMessage = null,
                    isSuccessMessage = false
                )
            }
            return
        }

        _uiState.update { it.copy(isSaving = true, globalMessage = null, isSuccessMessage = false) }

        viewModelScope.launch {
            val timestamp = System.currentTimeMillis()

            if (state.isEditMode && state.activityId != null) {
                activityRepository.updateActivity(
                    id = state.activityId,
                    name = state.activityName.trim(),
                    type = state.activityType,
                    durationMinutes = durationValue ?: 0,
                    calories = caloriesValue ?: 0,
                    timestampMillis = timestamp
                )

                _uiState.update {
                    it.copy(
                        isSaving = false,
                        globalMessage = "Activity updated successfully",
                        isSuccessMessage = true
                    )
                }
            } else {
                activityRepository.addActivity(
                    name = state.activityName.trim(),
                    type = state.activityType,
                    durationMinutes = durationValue ?: 0,
                    calories = caloriesValue ?: 0,
                    timestampMillis = timestamp
                )

                _uiState.update {
                    it.copy(
                        isSaving = false,
                        globalMessage = "Activity added successfully",
                        isSuccessMessage = true
                    )
                }
            }

            onSaved()
        }
    }

    private fun formatDate(timestampMillis: Long): String {
        return SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date(timestampMillis))
    }

    private fun formatTime(timestampMillis: Long): String {
        return SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date(timestampMillis))
    }
}


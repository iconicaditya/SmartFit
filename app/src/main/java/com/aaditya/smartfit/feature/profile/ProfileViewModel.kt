package com.aaditya.smartfit.feature.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aaditya.smartfit.data.repository.ProfileRepository
import com.aaditya.smartfit.data.repository.ProfileUpdateRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ProfileViewModel(
    private val profileRepository: ProfileRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    private var activeUserId: Long? = null

    fun bindUser(userId: Long) {
        if (activeUserId == userId) return
        activeUserId = userId

        viewModelScope.launch {
            profileRepository.profileFlow(userId).collect { profile ->
                _uiState.update { current ->
                    current.copy(
                        name = profile.name,
                        email = profile.email,
                        isDarkMode = profile.isDarkMode,
                        notificationsEnabled = profile.notificationsEnabled,
                        unitPreference = profile.unitPreference,
                        stepGoalInput = profile.stepGoal.toString(),
                        workoutGoalInput = profile.workoutGoal.toString(),
                        calorieGoalInput = profile.calorieGoal.toString(),
                        isLoading = false
                    )
                }
            }
        }
    }

    fun onEvent(event: ProfileUiEvent) {
        when (event) {
            is ProfileUiEvent.AvatarSelected -> {
                _uiState.update { it.copy(profileImageUri = event.uri, errorMessage = null, successMessage = null) }
            }

            is ProfileUiEvent.NameChanged -> {
                _uiState.update { it.copy(name = event.value, errorMessage = null, successMessage = null) }
            }

            is ProfileUiEvent.EmailChanged -> {
                _uiState.update { it.copy(email = event.value, errorMessage = null, successMessage = null) }
            }

            is ProfileUiEvent.DarkModeToggled -> {
                _uiState.update { it.copy(isDarkMode = event.enabled, successMessage = null) }
                viewModelScope.launch {
                    profileRepository.setThemeDark(event.enabled)
                }
            }

            is ProfileUiEvent.NotificationsToggled -> {
                _uiState.update { it.copy(notificationsEnabled = event.enabled, successMessage = null) }
            }

            is ProfileUiEvent.UnitPreferenceChanged -> {
                _uiState.update { it.copy(unitPreference = event.unitPreference, successMessage = null) }
            }

            is ProfileUiEvent.StepGoalInputChanged -> {
                _uiState.update {
                    it.copy(
                        stepGoalInput = event.value.filter(Char::isDigit).take(5),
                        errorMessage = null,
                        successMessage = null
                    )
                }
            }

            is ProfileUiEvent.WorkoutGoalInputChanged -> {
                _uiState.update {
                    it.copy(
                        workoutGoalInput = event.value.filter(Char::isDigit).take(3),
                        errorMessage = null,
                        successMessage = null
                    )
                }
            }

            is ProfileUiEvent.CalorieGoalInputChanged -> {
                _uiState.update {
                    it.copy(
                        calorieGoalInput = event.value.filter(Char::isDigit).take(4),
                        errorMessage = null,
                        successMessage = null
                    )
                }
            }

            ProfileUiEvent.SaveClicked -> submit()

            ProfileUiEvent.MessageDismissed -> {
                _uiState.update { it.copy(errorMessage = null, successMessage = null) }
            }

            ProfileUiEvent.BackClicked,
            ProfileUiEvent.AvatarClicked,
            ProfileUiEvent.LogoutClicked -> Unit
        }
    }

    private fun submit() {
        val state = _uiState.value
        val userId = activeUserId ?: return

        if (state.isSaving) return

        val trimmedName = state.name.trim()
        val trimmedEmail = state.email.trim()
        val stepGoal = state.stepGoalInput.toIntOrNull()
        val workoutGoal = state.workoutGoalInput.toIntOrNull()
        val calorieGoal = state.calorieGoalInput.toIntOrNull()

        val validationError = when {
            trimmedName.isBlank() -> "Please enter your name."
            !isValidEmail(trimmedEmail) -> "Please enter a valid email address."
            stepGoal == null -> "Enter a valid step goal."
            stepGoal !in PROFILE_STEP_GOAL_MIN..PROFILE_STEP_GOAL_MAX ->
                "Step goal must be between $PROFILE_STEP_GOAL_MIN and $PROFILE_STEP_GOAL_MAX."

            workoutGoal == null -> "Enter a valid workout duration goal."
            workoutGoal !in PROFILE_WORKOUT_GOAL_MIN..PROFILE_WORKOUT_GOAL_MAX ->
                "Workout goal must be between $PROFILE_WORKOUT_GOAL_MIN and $PROFILE_WORKOUT_GOAL_MAX minutes."

            calorieGoal == null -> "Enter a valid calorie intake goal."
            calorieGoal !in PROFILE_CALORIE_GOAL_MIN..PROFILE_CALORIE_GOAL_MAX ->
                "Calorie goal must be between $PROFILE_CALORIE_GOAL_MIN and $PROFILE_CALORIE_GOAL_MAX kcal."

            else -> null
        }

        if (validationError != null) {
            _uiState.update { it.copy(isSaving = false, errorMessage = validationError, successMessage = null) }
            return
        }

        _uiState.update { it.copy(isSaving = true, errorMessage = null, successMessage = null) }

        viewModelScope.launch {
            runCatching {
                profileRepository.updateProfile(
                    userId = userId,
                    request = ProfileUpdateRequest(
                        name = trimmedName,
                        email = trimmedEmail,
                        isDarkMode = state.isDarkMode,
                        notificationsEnabled = state.notificationsEnabled,
                        unitPreference = state.unitPreference,
                        stepGoal = stepGoal ?: PROFILE_STEP_GOAL_DEFAULT,
                        workoutGoal = workoutGoal ?: PROFILE_WORKOUT_GOAL_DEFAULT,
                        calorieGoal = calorieGoal ?: PROFILE_CALORIE_GOAL_DEFAULT
                    )
                )
            }.onSuccess {
                _uiState.update {
                    it.copy(
                        isSaving = false,
                        successMessage = "Preferences saved successfully.",
                        errorMessage = null
                    )
                }
            }.onFailure { throwable ->
                _uiState.update {
                    it.copy(
                        isSaving = false,
                        errorMessage = throwable.message ?: "Unable to save preferences",
                        successMessage = null
                    )
                }
            }
        }
    }

    private fun isValidEmail(input: String): Boolean {
        if (input.isBlank()) return false
        val atIndex = input.indexOf('@')
        return atIndex > 0 && atIndex < input.lastIndex && input.contains('.')
    }
}


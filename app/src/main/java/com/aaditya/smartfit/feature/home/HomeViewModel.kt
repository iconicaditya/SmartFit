package com.aaditya.smartfit.feature.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aaditya.smartfit.data.repository.ActivityRepository
import com.aaditya.smartfit.data.repository.ProfileData
import com.aaditya.smartfit.data.repository.ProfileRepository
import com.aaditya.smartfit.feature.activities.list.ActivityType
import com.aaditya.smartfit.feature.activities.list.FoodUiModel
import com.aaditya.smartfit.feature.profile.PROFILE_CALORIE_GOAL_DEFAULT
import com.aaditya.smartfit.feature.profile.PROFILE_STEP_GOAL_DEFAULT
import com.aaditya.smartfit.feature.profile.PROFILE_WORKOUT_GOAL_DEFAULT
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import java.util.Calendar

data class HomeSummaryData(
    val steps: Int,
    val caloriesIntake: Int,
    val caloriesBurned: Int,
    val workoutsCount: Int,
    val activeMinutes: Int,
    val recentActivities: List<HomeRecentActivity>
)

data class HomeRecentActivity(
    val title: String,
    val meta: String,
    val time: String,
    val type: ActivityType
)

class HomeViewModel(
    private val activityRepository: ActivityRepository,
    private val profileRepository: ProfileRepository
) : ViewModel() {

    private val _activeUserId = MutableStateFlow<Long?>(null)
    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    private val summaryData: StateFlow<HomeSummaryData> = combine(
        activityRepository.activitiesFlow,
        activityRepository.foodsFlow
    ) { activities, foods ->
        val now = System.currentTimeMillis()
        val oneDayMillis = 24L * 60L * 60L * 1000L

        val todayActivities = activities.filter { item ->
            val delta = now - item.timestampMillis
            delta in 0..oneDayMillis
        }

        val todayFoods: List<FoodUiModel> = foods.filter { item ->
            val delta = now - item.consumedAtMillis
            delta in 0..oneDayMillis
        }

        val estimatedSteps = todayActivities
            .sumOf { activity ->
                when (activity.type) {
                    ActivityType.WALKING -> activity.durationMinutes * 110
                    ActivityType.RUNNING -> activity.durationMinutes * 155
                    ActivityType.CYCLING -> activity.durationMinutes * 85
                    ActivityType.STRENGTH -> activity.durationMinutes * 65
                    ActivityType.YOGA -> activity.durationMinutes * 45
                }
            }
            .coerceAtLeast(0)

        val totalActiveMinutes = todayActivities.sumOf { it.durationMinutes }
        val caloriesBurned = todayActivities.sumOf { it.calories }

        val recent = todayActivities
            .sortedByDescending { it.timestampMillis }
            .take(3)
            .map { activity ->
                HomeRecentActivity(
                    title = activity.name,
                    meta = "${activity.durationMinutes} min • ${activity.calories} kcal",
                    time = formatTime(activity.timestampMillis),
                    type = activity.type
                )
            }

        HomeSummaryData(
            steps = estimatedSteps,
            caloriesIntake = todayFoods.sumOf { it.calories },
            caloriesBurned = caloriesBurned,
            workoutsCount = todayActivities.size,
            activeMinutes = totalActiveMinutes,
            recentActivities = recent
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = HomeSummaryData(
            steps = 0,
            caloriesIntake = 0,
            caloriesBurned = 0,
            workoutsCount = 0,
            activeMinutes = 0,
            recentActivities = emptyList()
        )
    )

    private val profileDataFlow: Flow<ProfileData?> = _activeUserId
        .flatMapLatest { userId ->
            if (userId == null) {
                flowOf(null)
            } else {
                profileRepository.profileFlow(userId)
            }
        }

    init {
        combine(
            summaryData,
            _activeUserId,
            profileDataFlow
        ) { summary, userId, profile ->
            HomeUiState(
                isLoading = userId != null && profile == null,
                greetingName = profile?.name?.substringBefore(' ')?.ifBlank { "User" } ?: "User",
                stepsToday = summary.steps,
                caloriesIntakeToday = summary.caloriesIntake,
                caloriesBurnedToday = summary.caloriesBurned,
                workoutsCount = summary.workoutsCount,
                activeMinutesToday = summary.activeMinutes,
                stepGoal = profile?.stepGoal ?: PROFILE_STEP_GOAL_DEFAULT,
                workoutGoalMinutes = profile?.workoutGoal ?: PROFILE_WORKOUT_GOAL_DEFAULT,
                calorieGoal = profile?.calorieGoal ?: PROFILE_CALORIE_GOAL_DEFAULT,
                recentActivities = summary.recentActivities
            )
        }
            .onEach { next -> _uiState.value = next }
            .launchIn(viewModelScope)
    }

    fun bindUser(userId: Long) {
        if (_activeUserId.value == userId) return
        _activeUserId.value = userId
    }

    private fun formatTime(timestampMillis: Long): String {
        val calendar = Calendar.getInstance().apply { timeInMillis = timestampMillis }
        val hour = calendar.get(Calendar.HOUR)
        val minute = calendar.get(Calendar.MINUTE)
        val amPm = if (calendar.get(Calendar.AM_PM) == Calendar.AM) "AM" else "PM"
        val normalizedHour = if (hour == 0) 12 else hour
        return "%d:%02d %s".format(normalizedHour, minute, amPm)
    }
}


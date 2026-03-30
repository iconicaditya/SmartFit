package com.aaditya.smartfit.feature.activities.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aaditya.smartfit.data.repository.ActivityRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ActivitiesScreenState(
    val selectedTab: ActivitiesTab = ActivitiesTab.ACTIVITIES,
    val activityFilterOption: ActivityFilterOption = ActivityFilterOption.ALL_TIME,
    val activitySortOption: ActivitySortOption = ActivitySortOption.NEWEST,
    val foodFilterOption: FoodFilterOption = FoodFilterOption.ALL_MEALS,
    val foodSortOption: FoodSortOption = FoodSortOption.NEWEST,
    val allActivities: List<ActivityUiModel> = emptyList(),
    val allFoods: List<FoodUiModel> = emptyList(),
    val deleteActivityTargetId: String? = null,
    val deleteFoodTargetId: String? = null,
    val sortMenuExpanded: Boolean = false,
    val lastDeletedActivity: ActivityUiModel? = null,
    val lastDeletedFood: FoodUiModel? = null,
    val isLoading: Boolean = true,
    val errorMessage: String? = null
)

class ActivitiesViewModel(
    private val activityRepository: ActivityRepository
) : ViewModel() {

    private val controlsState = MutableStateFlow(ActivitiesScreenState())

    val uiState: StateFlow<ActivitiesScreenState> = combine(
        controlsState,
        activityRepository.activitiesFlow,
        activityRepository.foodsFlow
    ) { controls, activities, foods ->
        controls.copy(
            allActivities = activities,
            allFoods = foods,
            isLoading = false,
            errorMessage = null
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = ActivitiesScreenState(isLoading = true)
    )

    init {
        viewModelScope.launch {
            runCatching { activityRepository.ensureSeedData() }
                .onFailure { throwable ->
                    controlsState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = throwable.message ?: "Unable to load activities"
                        )
                    }
                }
        }
    }

    fun refresh() {
        viewModelScope.launch {
            controlsState.update { it.copy(isLoading = true, errorMessage = null) }
            runCatching { activityRepository.ensureSeedData() }
                .onFailure { throwable ->
                    controlsState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = throwable.message ?: "Unable to refresh activities"
                        )
                    }
                }
        }
    }

    fun setSelectedTab(tab: ActivitiesTab) {
        controlsState.update { it.copy(selectedTab = tab, sortMenuExpanded = false) }
    }

    fun setActivityFilter(filter: ActivityFilterOption) {
        controlsState.update { it.copy(activityFilterOption = filter) }
    }

    fun setActivitySort(sort: ActivitySortOption) {
        controlsState.update { it.copy(activitySortOption = sort, sortMenuExpanded = false) }
    }

    fun setFoodFilter(filter: FoodFilterOption) {
        controlsState.update { it.copy(foodFilterOption = filter) }
    }

    fun setFoodSort(sort: FoodSortOption) {
        controlsState.update { it.copy(foodSortOption = sort, sortMenuExpanded = false) }
    }

    fun setSortMenuExpanded(expanded: Boolean) {
        controlsState.update { it.copy(sortMenuExpanded = expanded) }
    }

    fun requestDeleteActivity(id: String) {
        controlsState.update { it.copy(deleteActivityTargetId = id) }
    }

    fun requestDeleteFood(id: String) {
        controlsState.update { it.copy(deleteFoodTargetId = id) }
    }

    fun dismissDeleteDialogs() {
        controlsState.update { it.copy(deleteActivityTargetId = null, deleteFoodTargetId = null) }
    }

    fun confirmDeleteActivity() {
        val state = uiState.value
        val targetId = state.deleteActivityTargetId ?: return
        val deletedItem = state.allActivities.firstOrNull { it.id == targetId }

        viewModelScope.launch {
            activityRepository.deleteActivity(targetId)
            controlsState.update {
                it.copy(
                    deleteActivityTargetId = null,
                    lastDeletedActivity = deletedItem
                )
            }
        }
    }

    fun undoDeleteActivity() {
        val deleted = uiState.value.lastDeletedActivity ?: return
        viewModelScope.launch {
            activityRepository.upsertActivity(deleted)
            controlsState.update { it.copy(lastDeletedActivity = null) }
        }
    }

    fun consumeDeletedActivityUndoState() {
        controlsState.update { it.copy(lastDeletedActivity = null) }
    }

    fun confirmDeleteFood() {
        val state = uiState.value
        val targetId = state.deleteFoodTargetId ?: return
        val deletedItem = state.allFoods.firstOrNull { it.id == targetId }

        viewModelScope.launch {
            activityRepository.deleteFood(targetId)
            controlsState.update {
                it.copy(
                    deleteFoodTargetId = null,
                    lastDeletedFood = deletedItem
                )
            }
        }
    }

    fun undoDeleteFood() {
        val deleted = uiState.value.lastDeletedFood ?: return
        viewModelScope.launch {
            activityRepository.upsertFood(deleted)
            controlsState.update { it.copy(lastDeletedFood = null) }
        }
    }

    fun consumeDeletedFoodUndoState() {
        controlsState.update { it.copy(lastDeletedFood = null) }
    }
}


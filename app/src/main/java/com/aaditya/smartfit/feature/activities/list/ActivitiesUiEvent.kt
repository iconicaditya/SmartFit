package com.aaditya.smartfit.feature.activities.list

sealed interface ActivitiesUiEvent {
    data class OnTabChanged(
        val tab: ActivitiesTab
    ) : ActivitiesUiEvent

    data class OnFilterChanged(
        val filter: ActivityFilterOption
    ) : ActivitiesUiEvent

    data class OnSortChanged(
        val sort: ActivitySortOption
    ) : ActivitiesUiEvent

    data class OnFoodFilterChanged(
        val filter: FoodFilterOption
    ) : ActivitiesUiEvent

    data class OnFoodSortChanged(
        val sort: FoodSortOption
    ) : ActivitiesUiEvent

    data object OnAddClicked : ActivitiesUiEvent

    data object OnAddFoodClicked : ActivitiesUiEvent

    data class OnEditClicked(
        val activityId: String
    ) : ActivitiesUiEvent

    data class OnEditFoodClicked(
        val foodId: String
    ) : ActivitiesUiEvent

    data class OnDeleteRequested(
        val activityId: String
    ) : ActivitiesUiEvent

    data class OnDeleteFoodRequested(
        val foodId: String
    ) : ActivitiesUiEvent

    data class OnDeleteConfirmed(
        val activityId: String
    ) : ActivitiesUiEvent

    data class OnDeleteFoodConfirmed(
        val foodId: String
    ) : ActivitiesUiEvent

    data object OnDeleteDismissed : ActivitiesUiEvent

    data object OnRetryClicked : ActivitiesUiEvent
}

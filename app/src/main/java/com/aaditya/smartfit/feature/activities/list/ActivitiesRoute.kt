package com.aaditya.smartfit.feature.activities.list

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.aaditya.smartfit.data.di.SmartFitAppContainerProvider

@Composable
fun ActivitiesRoute(
    onAddActivityClick: () -> Unit,
    onEditActivityClick: (String) -> Unit,
    onAddFoodClick: () -> Unit,
    onEditFoodClick: (String) -> Unit
) {
    val context = LocalContext.current
    val appContainer = remember(context.applicationContext) {
        SmartFitAppContainerProvider.get(context.applicationContext)
    }

    val factory = remember(appContainer) {
        object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                if (modelClass.isAssignableFrom(ActivitiesViewModel::class.java)) {
                    @Suppress("UNCHECKED_CAST")
                    return ActivitiesViewModel(appContainer.activityRepository) as T
                }
                throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
            }
        }
    }

    val activitiesViewModel: ActivitiesViewModel = viewModel(factory = factory)
    val uiState by activitiesViewModel.uiState.collectAsState()

    ActivitiesScreen(
        uiState = uiState,
        onTabChanged = activitiesViewModel::setSelectedTab,
        onActivityFilterChanged = activitiesViewModel::setActivityFilter,
        onActivitySortChanged = activitiesViewModel::setActivitySort,
        onFoodFilterChanged = activitiesViewModel::setFoodFilter,
        onFoodSortChanged = activitiesViewModel::setFoodSort,
        onRefreshClick = activitiesViewModel::refresh,
        onSortMenuExpandedChanged = activitiesViewModel::setSortMenuExpanded,
        onRequestDeleteActivity = activitiesViewModel::requestDeleteActivity,
        onRequestDeleteFood = activitiesViewModel::requestDeleteFood,
        onDismissDeleteDialogs = activitiesViewModel::dismissDeleteDialogs,
        onConfirmDeleteActivity = activitiesViewModel::confirmDeleteActivity,
        onConfirmDeleteFood = activitiesViewModel::confirmDeleteFood,
        onUndoDeleteActivity = activitiesViewModel::undoDeleteActivity,
        onUndoDeleteFood = activitiesViewModel::undoDeleteFood,
        onConsumeDeletedActivityUndo = activitiesViewModel::consumeDeletedActivityUndoState,
        onConsumeDeletedFoodUndo = activitiesViewModel::consumeDeletedFoodUndoState,
        onAddActivityClick = onAddActivityClick,
        onEditActivityClick = onEditActivityClick,
        onAddFoodClick = onAddFoodClick,
        onEditFoodClick = onEditFoodClick
    )
}


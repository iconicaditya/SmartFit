package com.aaditya.smartfit.feature.activities.food

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.aaditya.smartfit.data.di.SmartFitAppContainerProvider

@Composable
fun FoodFormRoute(
    foodId: String?,
    onBackClick: () -> Unit
) {
    val context = LocalContext.current
    val appContainer = remember(context.applicationContext) {
        SmartFitAppContainerProvider.get(context.applicationContext)
    }

    val factory = remember(appContainer) {
        object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                if (modelClass.isAssignableFrom(FoodFormViewModel::class.java)) {
                    @Suppress("UNCHECKED_CAST")
                    return FoodFormViewModel(appContainer.activityRepository) as T
                }
                throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
            }
        }
    }

    val formViewModel: FoodFormViewModel = viewModel(factory = factory)
    val uiState by formViewModel.uiState.collectAsState()

    LaunchedEffect(foodId) {
        formViewModel.load(foodId)
    }

    FoodFormScreen(
        uiState = uiState,
        onEvent = { event ->
            when (event) {
                FoodFormUiEvent.OnSubmitClicked -> formViewModel.onEvent(event, onBackClick)
                else -> formViewModel.onEvent(event)
            }
        },
        onBackClick = onBackClick
    )
}


package com.aaditya.smartfit.feature.home

import android.net.Uri
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
fun HomeRoute(
    profileImageUri: Uri? = null,
    onAddActivityClick: () -> Unit = {},
    onViewActivitiesClick: () -> Unit = {},
    onViewTipsClick: () -> Unit = {}
) {
    val context = LocalContext.current
    val appContainer = remember(context.applicationContext) {
        SmartFitAppContainerProvider.get(context.applicationContext)
    }

    val factory = remember(appContainer) {
        object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
                    @Suppress("UNCHECKED_CAST")
                    return HomeViewModel(
                        activityRepository = appContainer.activityRepository,
                        profileRepository = appContainer.profileRepository
                    ) as T
                }
                throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
            }
        }
    }

    val homeViewModel: HomeViewModel = viewModel(factory = factory)
    val uiState by homeViewModel.uiState.collectAsState()

    LaunchedEffect(appContainer) {
        val userId = appContainer.authRepository.currentUserId()
        if (userId != null) {
            homeViewModel.bindUser(userId)
        }
    }

    HomeScreen(
        uiState = uiState,
        profileImageUri = profileImageUri,
        onAddActivityClick = onAddActivityClick,
        onViewActivitiesClick = onViewActivitiesClick,
        onViewTipsClick = onViewTipsClick
    )
}


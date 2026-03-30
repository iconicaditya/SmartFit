package com.aaditya.smartfit.feature.profile

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
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
fun ProfileRoute(
    profileImageUri: Uri? = null,
    onProfileImageChanged: (Uri?) -> Unit = {},
    onBack: () -> Unit = {},
    onToggleTheme: (Boolean) -> Unit = {},
    onLogout: () -> Unit = {}
) {
    val context = LocalContext.current
    val appContainer = remember(context.applicationContext) {
        SmartFitAppContainerProvider.get(context.applicationContext)
    }

    val factory = remember(appContainer) {
        object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                if (modelClass.isAssignableFrom(ProfileViewModel::class.java)) {
                    @Suppress("UNCHECKED_CAST")
                    return ProfileViewModel(appContainer.profileRepository) as T
                }
                throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
            }
        }
    }

    val profileViewModel: ProfileViewModel = viewModel(factory = factory)
    val uiState by profileViewModel.uiState.collectAsState()

    LaunchedEffect(appContainer) {
        val userId = appContainer.authRepository.currentUserId()
        if (userId != null) {
            profileViewModel.bindUser(userId)
        } else {
            onLogout()
        }
    }

    LaunchedEffect(profileImageUri) {
        if (profileImageUri != uiState.profileImageUri) {
            profileViewModel.onEvent(ProfileUiEvent.AvatarSelected(profileImageUri))
        }
    }

    LaunchedEffect(uiState.isDarkMode) {
        onToggleTheme(uiState.isDarkMode)
    }

    val avatarPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            profileViewModel.onEvent(ProfileUiEvent.AvatarSelected(uri))
            onProfileImageChanged(uri)
        }
    }

    ProfileScreen(
        uiState = uiState,
        onEvent = { event ->
            when (event) {
                ProfileUiEvent.BackClicked -> onBack()

                ProfileUiEvent.AvatarClicked -> {
                    avatarPickerLauncher.launch(
                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                    )
                }

                is ProfileUiEvent.AvatarSelected -> {
                    profileViewModel.onEvent(event)
                    onProfileImageChanged(event.uri)
                }

                ProfileUiEvent.LogoutClicked -> onLogout()

                else -> profileViewModel.onEvent(event)
            }
        }
    )
}

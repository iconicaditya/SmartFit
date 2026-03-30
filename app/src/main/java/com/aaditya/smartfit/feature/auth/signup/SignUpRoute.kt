package com.aaditya.smartfit.feature.auth.signup

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
fun SignUpRoute(
    onBackToLoginClick: () -> Unit
) {
    val context = LocalContext.current
    val appContainer = remember(context.applicationContext) {
        SmartFitAppContainerProvider.get(context.applicationContext)
    }

    val factory = remember(appContainer) {
        object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                if (modelClass.isAssignableFrom(SignUpViewModel::class.java)) {
                    @Suppress("UNCHECKED_CAST")
                    return SignUpViewModel(appContainer.authRepository) as T
                }
                throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
            }
        }
    }

    val signUpViewModel: SignUpViewModel = viewModel(factory = factory)
    val uiState by signUpViewModel.uiState.collectAsState()

    SignUpScreen(
        uiState = uiState,
        onEvent = signUpViewModel::onEvent,
        onBackToLoginClick = onBackToLoginClick
    )
}


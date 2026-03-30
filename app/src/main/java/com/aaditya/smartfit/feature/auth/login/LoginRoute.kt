package com.aaditya.smartfit.feature.auth.login

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
fun LoginRoute(
    onBackClick: () -> Unit,
    onSignUpClick: () -> Unit,
    onLoginSuccess: () -> Unit
) {
    val context = LocalContext.current
    val appContainer = remember(context.applicationContext) {
        SmartFitAppContainerProvider.get(context.applicationContext)
    }

    val factory = remember(appContainer) {
        object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {
                    @Suppress("UNCHECKED_CAST")
                    return LoginViewModel(appContainer.authRepository) as T
                }
                throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
            }
        }
    }

    val loginViewModel: LoginViewModel = viewModel(factory = factory)
    val uiState by loginViewModel.uiState.collectAsState()

    LoginScreen(
        uiState = uiState,
        onEvent = { event ->
            when (event) {
                LoginUiEvent.Submit -> loginViewModel.onEvent(event, onLoginSuccess)
                else -> loginViewModel.onEvent(event)
            }
        },
        onBackClick = onBackClick,
        onSignUpClick = onSignUpClick
    )
}

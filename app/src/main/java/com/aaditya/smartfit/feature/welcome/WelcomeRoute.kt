package com.aaditya.smartfit.feature.welcome

import androidx.compose.runtime.Composable

@Composable
fun WelcomeRoute(
    onGetStartedClick: () -> Unit
) {
    WelcomeScreen(onGetStartedClick = onGetStartedClick)
}

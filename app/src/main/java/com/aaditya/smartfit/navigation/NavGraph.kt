package com.aaditya.smartfit.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.aaditya.smartfit.ui.screens.activitylog.ActivityLogScreen
import com.aaditya.smartfit.ui.screens.addactivity.AddActivityScreen
import com.aaditya.smartfit.ui.screens.home.HomeScreen
import com.aaditya.smartfit.ui.screens.profile.ProfileScreen
import com.aaditya.smartfit.ui.screens.Tips.TipsScreen
import com.aaditya.smartfit.ui.screens.welcome.LoginScreen
import com.aaditya.smartfit.ui.screens.welcome.RegisterScreen
import com.aaditya.smartfit.ui.screens.welcome.WelcomeScreen
import com.aaditya.smartfit.ui.screens.main.MainScaffold

@Composable
fun NavGraph() {
    val navController = rememberNavController()

    MainScaffold(navController = navController) {
        NavHost(
            navController = navController,
            startDestination = Routes.LOGIN
        ) {
            composable(Routes.WELCOME) {
                WelcomeScreen(onContinue = { navController.navigate(Routes.LOGIN) })
            }
            composable(Routes.LOGIN) {
                LoginScreen(
                    onLoginClick = {
                        navController.navigate(Routes.HOME) {
                            popUpTo(Routes.LOGIN) { inclusive = true }
                        }
                    },
                    onRegisterClick = { navController.navigate(Routes.REGISTER) }
                )
            }
            composable(Routes.REGISTER) {
                RegisterScreen(
                    onRegisterClick = {
                        navController.navigate(Routes.HOME) {
                            popUpTo(Routes.LOGIN) { inclusive = true }
                        }
                    },
                    onBackToLoginClick = { navController.popBackStack() }
                )
            }
            composable(Routes.HOME) {
                HomeScreen(
                    onOpenAddActivity = { navController.navigate(Routes.ADD_ACTIVITY) },
                    onOpenActivityLog = { navController.navigate(Routes.ACTIVITY_LOG) },
                    onOpenWeeklySummary = { navController.navigate(Routes.TIPS) }
                )
            }
            composable(Routes.ACTIVITY_LOG) {
                ActivityLogScreen()
            }
            composable(Routes.TIPS) {
                TipsScreen()
            }
            composable(Routes.PROFILE) {
                ProfileScreen()
            }
            composable(Routes.ADD_ACTIVITY) {
                AddActivityScreen(onBack = { navController.popBackStack() })
            }
        }
    }
}

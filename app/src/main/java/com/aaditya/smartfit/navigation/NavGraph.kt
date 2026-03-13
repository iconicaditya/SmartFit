package com.aaditya.smartfit.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.aaditya.smartfit.ui.screens.activitylog.ActivityLogScreen
import com.aaditya.smartfit.ui.screens.addactivity.AddActivityScreen
import com.aaditya.smartfit.ui.screens.home.HomeScreen
import com.aaditya.smartfit.ui.screens.profile.ProfileScreen
import com.aaditya.smartfit.ui.screens.welcome.LoginScreen
import com.aaditya.smartfit.ui.screens.welcome.WelcomeScreen

private const val ROUTE_WELCOME = "welcome"
private const val ROUTE_LOGIN = "login"
private const val ROUTE_HOME = "home"
private const val ROUTE_ACTIVITY_LOG = "activity_log"
private const val ROUTE_ADD_ACTIVITY = "add_activity"
private const val ROUTE_PROFILE = "profile"

@Composable
fun NavGraph() {
    var currentRoute by remember { mutableStateOf(ROUTE_LOGIN) }

    when (currentRoute) {
        ROUTE_WELCOME -> WelcomeScreen(onContinue = { currentRoute = ROUTE_LOGIN })
        ROUTE_LOGIN -> LoginScreen(onLoginClick = { currentRoute = ROUTE_HOME })
        ROUTE_HOME -> HomeScreen(
            onOpenActivityLog = { currentRoute = ROUTE_ACTIVITY_LOG },
            onOpenAddActivity = { currentRoute = ROUTE_ADD_ACTIVITY },
            onOpenProfile = { currentRoute = ROUTE_PROFILE }
        )
        ROUTE_ACTIVITY_LOG -> ActivityLogScreen(onBack = { currentRoute = ROUTE_HOME })
        ROUTE_ADD_ACTIVITY -> AddActivityScreen(onBack = { currentRoute = ROUTE_HOME })
        ROUTE_PROFILE -> ProfileScreen(onBack = { currentRoute = ROUTE_HOME })
    }
}


package com.aaditya.smartfit.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EmojiObjects
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.EmojiObjects
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.List
import androidx.compose.material.icons.outlined.Person
import androidx.compose.ui.graphics.vector.ImageVector

object Routes {
    const val WELCOME = "welcome"
    const val LOGIN = "login"
    const val REGISTER = "register"
    const val HOME = "home"
    const val ACTIVITY_LOG = "activity_log"
    const val TIPS = "tips"
    const val PROFILE = "profile"
    const val ADD_ACTIVITY = "add_activity"
}

data class BottomNavDestination(
    val route: String,
    val label: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
)

val bottomNavDestinations = listOf(
    BottomNavDestination(
        route = Routes.HOME,
        label = "Home",
        selectedIcon = Icons.Filled.Home,
        unselectedIcon = Icons.Outlined.Home
    ),
    BottomNavDestination(
        route = Routes.ACTIVITY_LOG,
        label = "Activity",
        selectedIcon = Icons.Filled.List,
        unselectedIcon = Icons.Outlined.List
    ),
    BottomNavDestination(
        route = Routes.TIPS,
        label = "Tips",
        selectedIcon = Icons.Filled.EmojiObjects,
        unselectedIcon = Icons.Outlined.EmojiObjects
    ),
    BottomNavDestination(
        route = Routes.PROFILE,
        label = "Profile",
        selectedIcon = Icons.Filled.Person,
        unselectedIcon = Icons.Outlined.Person
    )
)


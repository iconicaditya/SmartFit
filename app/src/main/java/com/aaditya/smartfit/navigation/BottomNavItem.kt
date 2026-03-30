package com.aaditya.smartfit.navigation

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.TipsAndUpdates
import androidx.compose.ui.graphics.vector.ImageVector
import com.aaditya.smartfit.R

data class BottomNavItem(
    val route: String,
    @StringRes val labelResId: Int,
    val icon: ImageVector
)

val smartFitBottomNavItems = listOf(
    BottomNavItem(
        route = NavRoutes.HOME,
        labelResId = R.string.nav_home,
        icon = Icons.Filled.Home
    ),
    BottomNavItem(
        route = NavRoutes.ACTIVITIES,
        labelResId = R.string.nav_activities,
        icon = Icons.Filled.List
    ),
    BottomNavItem(
        route = NavRoutes.TIPS,
        labelResId = R.string.nav_tips,
        icon = Icons.Filled.TipsAndUpdates
    ),
    BottomNavItem(
        route = NavRoutes.PROFILE,
        labelResId = R.string.nav_profile,
        icon = Icons.Filled.Person
    )
)


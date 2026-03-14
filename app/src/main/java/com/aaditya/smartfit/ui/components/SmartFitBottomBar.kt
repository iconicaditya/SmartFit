package com.aaditya.smartfit.ui.components

import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.aaditya.smartfit.navigation.bottomNavDestinations

@Composable
fun SmartFitBottomBar(
    currentRoute: String?,
    onNavigate: (String) -> Unit
) {
    NavigationBar(
        containerColor = Color(0xFF101820),
        contentColor = Color.White
    ) {
        bottomNavDestinations.forEach { destination ->
            val selected = currentRoute == destination.route
            NavigationBarItem(
                selected = selected,
                onClick = { onNavigate(destination.route) },
                icon = {
                    Icon(
                        imageVector = if (selected) destination.selectedIcon else destination.unselectedIcon,
                        contentDescription = destination.label
                    )
                },
                label = { Text(text = destination.label) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Color(0xFF00C9A7),
                    selectedTextColor = Color(0xFF00C9A7),
                    unselectedIconColor = Color(0xFF8B949E),
                    unselectedTextColor = Color(0xFF8B949E),
                    indicatorColor = Color(0x222AD7BE)
                )
            )
        }
    }
}


package com.aaditya.smartfit.navigation

import android.net.Uri
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.NavHost
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.aaditya.smartfit.feature.activities.food.FoodFormRoute
import com.aaditya.smartfit.feature.activities.form.ActivityFormRoute
import com.aaditya.smartfit.feature.activities.list.ActivitiesRoute
import com.aaditya.smartfit.feature.auth.login.LoginRoute
import com.aaditya.smartfit.feature.auth.signup.SignUpRoute
import com.aaditya.smartfit.feature.home.HomeRoute
import com.aaditya.smartfit.feature.profile.ProfileRoute
import com.aaditya.smartfit.feature.tips.TipsRoute
import com.aaditya.smartfit.feature.welcome.WelcomeRoute
import com.aaditya.smartfit.data.di.SmartFitAppContainerProvider
import kotlinx.coroutines.launch

@Composable
fun SmartFitNavGraph(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    onToggleTheme: (Boolean) -> Unit = {},
    profileImageUri: Uri? = null,
    onProfileImageChanged: (Uri?) -> Unit = {}
) {
    val context = LocalContext.current
    val appContainer = remember(context.applicationContext) {
        SmartFitAppContainerProvider.get(context.applicationContext)
    }
    val coroutineScope = rememberCoroutineScope()

    var hasActiveSession by remember { mutableStateOf(false) }
    var hasCheckedSession by remember { mutableStateOf(false) }

    LaunchedEffect(appContainer) {
        hasActiveSession = appContainer.authRepository.isLoggedIn()
        hasCheckedSession = true
    }

    val backStackEntry = navController.currentBackStackEntryAsState().value
    val currentRoute = backStackEntry?.destination?.route

    LaunchedEffect(hasCheckedSession, hasActiveSession, currentRoute) {
        if (hasCheckedSession && hasActiveSession && currentRoute == NavRoutes.WELCOME) {
            navController.navigate(NavRoutes.HOME) {
                popUpTo(NavRoutes.WELCOME) { inclusive = true }
                launchSingleTop = true
            }
        }
    }

    val showBottomBar = currentRoute in setOf(
        NavRoutes.HOME,
        NavRoutes.ACTIVITIES,
        NavRoutes.TIPS,
        NavRoutes.PROFILE
    )

    Scaffold(
        modifier = modifier,
        bottomBar = {
            if (showBottomBar) {
                NavigationBar {
                    smartFitBottomNavItems.forEach { item ->
                        val selected = currentRoute == item.route
                        NavigationBarItem(
                            selected = selected,
                            onClick = {
                                if (!selected) {
                                    navController.navigate(item.route) {
                                        popUpTo(NavRoutes.HOME) { saveState = true }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                }
                            },
                            icon = {
                                Icon(
                                    imageVector = item.icon,
                                    contentDescription = stringResource(id = item.labelResId)
                                )
                            },
                            label = {
                                Text(text = stringResource(id = item.labelResId))
                            }
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = NavRoutes.WELCOME,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(route = NavRoutes.WELCOME) {
                WelcomeRoute(
                    onGetStartedClick = {
                        navController.navigate(NavRoutes.LOGIN)
                    }
                )
            }

            composable(route = NavRoutes.LOGIN) {
                LoginRoute(
                    onBackClick = {
                        navController.popBackStack()
                    },
                    onSignUpClick = {
                        navController.navigate(NavRoutes.SIGN_UP)
                    },
                    onLoginSuccess = {
                        hasActiveSession = true
                        navController.navigate(NavRoutes.HOME) {
                            popUpTo(NavRoutes.WELCOME) { inclusive = true }
                            launchSingleTop = true
                        }
                    }
                )
            }

            composable(route = NavRoutes.SIGN_UP) {
                SignUpRoute(
                    onBackToLoginClick = {
                        navController.popBackStack()
                    }
                )
            }

            composable(route = NavRoutes.HOME) {
                HomeRoute(
                    profileImageUri = profileImageUri,
                    onAddActivityClick = {
                        navController.navigate(NavRoutes.ACTIVITY_ADD)
                    },
                    onViewActivitiesClick = {
                        navController.navigate(NavRoutes.ACTIVITIES) {
                            popUpTo(NavRoutes.HOME) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    onViewTipsClick = {
                        navController.navigate(NavRoutes.TIPS) {
                            popUpTo(NavRoutes.HOME) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }

            composable(route = NavRoutes.ACTIVITIES) {
                ActivitiesRoute(
                    onAddActivityClick = {
                        navController.navigate(NavRoutes.ACTIVITY_ADD)
                    },
                    onEditActivityClick = { activityId ->
                        navController.navigate(NavRoutes.activityEditRoute(activityId))
                    },
                    onAddFoodClick = {
                        navController.navigate(NavRoutes.FOOD_ADD)
                    },
                    onEditFoodClick = { foodId ->
                        navController.navigate(NavRoutes.foodEditRoute(foodId))
                    }
                )
            }

            composable(route = NavRoutes.ACTIVITY_ADD) {
                ActivityFormRoute(
                    activityId = null,
                    onBackClick = {
                        navController.popBackStack()
                    }
                )
            }

            composable(
                route = NavRoutes.ACTIVITY_EDIT,
                arguments = listOf(
                    navArgument("activityId") {
                        type = NavType.StringType
                    }
                )
            ) { backStack ->
                val activityId = backStack.arguments?.getString("activityId")
                ActivityFormRoute(
                    activityId = activityId,
                    onBackClick = {
                        navController.popBackStack()
                    }
                )
            }

            composable(route = NavRoutes.FOOD_ADD) {
                FoodFormRoute(
                    foodId = null,
                    onBackClick = {
                        navController.popBackStack()
                    }
                )
            }

            composable(
                route = NavRoutes.FOOD_EDIT,
                arguments = listOf(
                    navArgument("foodId") {
                        type = NavType.StringType
                    }
                )
            ) { backStack ->
                val foodId = backStack.arguments?.getString("foodId")
                FoodFormRoute(
                    foodId = foodId,
                    onBackClick = {
                        navController.popBackStack()
                    }
                )
            }

            composable(route = NavRoutes.TIPS) {
                TipsRoute()
            }

            composable(route = NavRoutes.PROFILE) {
                ProfileRoute(
                    profileImageUri = profileImageUri,
                    onProfileImageChanged = onProfileImageChanged,
                    onBack = { navController.popBackStack() },
                    onToggleTheme = { enabled -> onToggleTheme(enabled) },
                    onLogout = {
                        coroutineScope.launch {
                            appContainer.authRepository.logout()
                            hasActiveSession = false
                            navController.navigate(NavRoutes.WELCOME) {
                                popUpTo(NavRoutes.WELCOME) { inclusive = true }
                                launchSingleTop = true
                            }
                        }
                    }
                )
            }
        }
    }
}

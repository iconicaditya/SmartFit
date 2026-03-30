package com.aaditya.smartfit.navigation

object NavRoutes {
    const val WELCOME = "welcome"
    const val LOGIN = "login"
    const val SIGN_UP = "signup"
    const val HOME = "home"
    const val ACTIVITIES = "activities"
    const val ACTIVITY_ADD = "activity/add"
    const val ACTIVITY_EDIT = "activity/edit/{activityId}"
    const val FOOD_ADD = "food/add"
    const val FOOD_EDIT = "food/edit/{foodId}"
    const val TIPS = "tips"
    const val PROFILE = "profile"

    fun activityEditRoute(activityId: String): String {
        return "activity/edit/$activityId"
    }

    fun foodEditRoute(foodId: String): String {
        return "food/edit/$foodId"
    }
}

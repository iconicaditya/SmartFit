package com.aaditya.smartfit.data.remote

import com.aaditya.smartfit.data.model.WorkoutTip

object RetrofitInstance {
    // Placeholder provider so the project stays dependency-light while keeping target structure.
    val apiService: ApiService = object : ApiService {
        override fun getWorkoutTips(): List<WorkoutTip> {
            return listOf(
                WorkoutTip(1, "Stay Hydrated", "Drink water before and after workouts."),
                WorkoutTip(2, "Warm Up", "Start with 5 minutes of dynamic stretches.")
            )
        }
    }
}


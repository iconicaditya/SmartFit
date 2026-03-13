package com.aaditya.smartfit.data.remote

import com.aaditya.smartfit.data.model.WorkoutTip

interface ApiService {
    fun getWorkoutTips(): List<WorkoutTip>
}


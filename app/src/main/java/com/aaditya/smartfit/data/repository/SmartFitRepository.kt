package com.aaditya.smartfit.data.repository

import com.aaditya.smartfit.data.local.ActivityDao
import com.aaditya.smartfit.data.local.ActivityEntity
import com.aaditya.smartfit.data.model.Activity
import com.aaditya.smartfit.data.model.WorkoutTip
import com.aaditya.smartfit.data.remote.ApiService

class SmartFitRepository(
    private val activityDao: ActivityDao,
    private val apiService: ApiService
) {
    fun getActivities(): List<Activity> {
        return activityDao.getAllActivities().map { entity ->
            Activity(
                id = entity.id,
                name = entity.name,
                durationMinutes = entity.durationMinutes,
                caloriesBurned = entity.caloriesBurned,
                timestampMillis = entity.timestampMillis
            )
        }
    }

    fun addActivity(activity: Activity) {
        activityDao.insertActivity(
            ActivityEntity(
                id = activity.id,
                name = activity.name,
                durationMinutes = activity.durationMinutes,
                caloriesBurned = activity.caloriesBurned,
                timestampMillis = activity.timestampMillis
            )
        )
    }

    fun getWorkoutTips(): List<WorkoutTip> = apiService.getWorkoutTips()
}


package com.aaditya.smartfit.data.local

class SmartFitDatabase {
    private val activities = mutableListOf<ActivityEntity>()

    val activityDao: ActivityDao = object : ActivityDao {
        override fun getAllActivities(): List<ActivityEntity> = activities.toList()

        override fun insertActivity(activity: ActivityEntity) {
            activities.add(activity)
        }
    }
}


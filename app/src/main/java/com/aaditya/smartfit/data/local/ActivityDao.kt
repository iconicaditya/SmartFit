package com.aaditya.smartfit.data.local

interface ActivityDao {
    fun getAllActivities(): List<ActivityEntity>
    fun insertActivity(activity: ActivityEntity)
}


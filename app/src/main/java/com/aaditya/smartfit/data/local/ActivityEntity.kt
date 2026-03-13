package com.aaditya.smartfit.data.local

data class ActivityEntity(
    val id: Long,
    val name: String,
    val durationMinutes: Int,
    val caloriesBurned: Int,
    val timestampMillis: Long
)


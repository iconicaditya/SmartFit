package com.aaditya.smartfit.data.model

data class Activity(
    val id: Long,
    val name: String,
    val durationMinutes: Int,
    val caloriesBurned: Int,
    val timestampMillis: Long
)


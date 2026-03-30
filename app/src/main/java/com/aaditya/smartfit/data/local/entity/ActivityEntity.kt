package com.aaditya.smartfit.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "activities")
data class ActivityEntity(
    @PrimaryKey
    val id: String,
    val name: String,
    val type: String,
    val durationMinutes: Int,
    val caloriesBurned: Int,
    val timestampMillis: Long
)


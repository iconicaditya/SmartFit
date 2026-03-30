package com.aaditya.smartfit.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "plans")
data class PlanEntity(
    @PrimaryKey
    val id: String,
    val name: String,
    val durationMinutes: Int,
    val calories: Int,
    val frequency: String,
    val timeMillis: Long
)


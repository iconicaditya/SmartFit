package com.aaditya.smartfit.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "foods")
data class FoodEntity(
    @PrimaryKey
    val id: String,
    val name: String,
    val caloriesIntake: Int,
    val mealType: String,
    val consumedAtMillis: Long,
    val notes: String?
)


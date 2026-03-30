package com.aaditya.smartfit.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.aaditya.smartfit.data.local.dao.ActivityDao
import com.aaditya.smartfit.data.local.dao.FoodDao
import com.aaditya.smartfit.data.local.dao.PlanDao
import com.aaditya.smartfit.data.local.dao.UserDao
import com.aaditya.smartfit.data.local.entity.ActivityEntity
import com.aaditya.smartfit.data.local.entity.FoodEntity
import com.aaditya.smartfit.data.local.entity.PlanEntity
import com.aaditya.smartfit.data.local.entity.UserEntity

@Database(
    entities = [
        ActivityEntity::class,
        FoodEntity::class,
        PlanEntity::class,
        UserEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class SmartFitDatabase : RoomDatabase() {
    abstract fun activityDao(): ActivityDao
    abstract fun foodDao(): FoodDao
    abstract fun planDao(): PlanDao
    abstract fun userDao(): UserDao
}


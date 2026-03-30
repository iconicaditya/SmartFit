package com.aaditya.smartfit.data.repository.impl

import com.aaditya.smartfit.data.local.dao.ActivityDao
import com.aaditya.smartfit.data.local.dao.FoodDao
import com.aaditya.smartfit.data.local.entity.ActivityEntity
import com.aaditya.smartfit.data.local.entity.FoodEntity
import com.aaditya.smartfit.data.repository.ActivityRepository
import com.aaditya.smartfit.feature.activities.list.ActivitiesMockData
import com.aaditya.smartfit.feature.activities.list.ActivityType
import com.aaditya.smartfit.feature.activities.list.ActivityUiModel
import com.aaditya.smartfit.feature.activities.list.FoodUiModel
import com.aaditya.smartfit.feature.activities.list.MealType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.UUID

class RoomActivityRepository(
    private val activityDao: ActivityDao,
    private val foodDao: FoodDao
) : ActivityRepository {

    override val activitiesFlow: Flow<List<ActivityUiModel>> =
        activityDao.observeAll().map { entities -> entities.map { entity -> entity.toUiModel() } }

    override val foodsFlow: Flow<List<FoodUiModel>> =
        foodDao.observeAll().map { entities -> entities.map { entity -> entity.toUiModel() } }

    override suspend fun ensureSeedData() {
        if (activityDao.count() == 0) {
            activityDao.insertAll(ActivitiesMockData.activities.map { it.toEntity() })
        }
        if (foodDao.count() == 0) {
            foodDao.insertAll(ActivitiesMockData.foods.map { it.toEntity() })
        }
    }

    override suspend fun getActivityById(id: String): ActivityUiModel? {
        return activityDao.findById(id)?.toUiModel()
    }

    override suspend fun getFoodById(id: String): FoodUiModel? {
        return foodDao.findById(id)?.toUiModel()
    }

    override suspend fun addActivity(
        name: String,
        type: ActivityType,
        durationMinutes: Int,
        calories: Int,
        timestampMillis: Long
    ) {
        activityDao.insert(
            ActivityEntity(
                id = "a_${UUID.randomUUID()}",
                name = name,
                type = type.name,
                durationMinutes = durationMinutes,
                caloriesBurned = calories,
                timestampMillis = timestampMillis
            )
        )
    }

    override suspend fun updateActivity(
        id: String,
        name: String,
        type: ActivityType,
        durationMinutes: Int,
        calories: Int,
        timestampMillis: Long
    ) {
        activityDao.insert(
            ActivityEntity(
                id = id,
                name = name,
                type = type.name,
                durationMinutes = durationMinutes,
                caloriesBurned = calories,
                timestampMillis = timestampMillis
            )
        )
    }

    override suspend fun deleteActivity(id: String) {
        activityDao.deleteById(id)
    }

    override suspend fun upsertActivity(item: ActivityUiModel) {
        activityDao.insert(item.toEntity())
    }

    override suspend fun addFood(
        name: String,
        calories: Int,
        mealType: MealType,
        consumedAtMillis: Long,
        notes: String?
    ) {
        foodDao.insert(
            FoodEntity(
                id = "f_${UUID.randomUUID()}",
                name = name,
                caloriesIntake = calories,
                mealType = mealType.name,
                consumedAtMillis = consumedAtMillis,
                notes = notes
            )
        )
    }

    override suspend fun updateFood(
        id: String,
        name: String,
        calories: Int,
        mealType: MealType,
        consumedAtMillis: Long,
        notes: String?
    ) {
        foodDao.insert(
            FoodEntity(
                id = id,
                name = name,
                caloriesIntake = calories,
                mealType = mealType.name,
                consumedAtMillis = consumedAtMillis,
                notes = notes
            )
        )
    }

    override suspend fun deleteFood(id: String) {
        foodDao.deleteById(id)
    }

    override suspend fun upsertFood(item: FoodUiModel) {
        foodDao.insert(item.toEntity())
    }
}

private fun ActivityEntity.toUiModel(): ActivityUiModel {
    return ActivityUiModel(
        id = id,
        name = name,
        type = runCatching { ActivityType.valueOf(type) }.getOrDefault(ActivityType.WALKING),
        durationMinutes = durationMinutes,
        calories = caloriesBurned,
        timestampMillis = timestampMillis
    )
}

private fun FoodEntity.toUiModel(): FoodUiModel {
    return FoodUiModel(
        id = id,
        name = name,
        calories = caloriesIntake,
        mealType = runCatching { MealType.valueOf(mealType) }.getOrDefault(MealType.BREAKFAST),
        consumedAtMillis = consumedAtMillis,
        notes = notes
    )
}

private fun ActivityUiModel.toEntity(): ActivityEntity {
    return ActivityEntity(
        id = id,
        name = name,
        type = type.name,
        durationMinutes = durationMinutes,
        caloriesBurned = calories,
        timestampMillis = timestampMillis
    )
}

private fun FoodUiModel.toEntity(): FoodEntity {
    return FoodEntity(
        id = id,
        name = name,
        caloriesIntake = calories,
        mealType = mealType.name,
        consumedAtMillis = consumedAtMillis,
        notes = notes
    )
}


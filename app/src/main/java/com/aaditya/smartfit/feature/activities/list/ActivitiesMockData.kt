package com.aaditya.smartfit.feature.activities.list

object ActivitiesMockData {
    val activities: List<ActivityUiModel> = listOf(
        ActivityUiModel(
            id = "a_1",
            name = "Morning Walk",
            type = ActivityType.WALKING,
            durationMinutes = 35,
            calories = 180,
            timestampMillis = 1711733400000
        ),
        ActivityUiModel(
            id = "a_2",
            name = "Evening Run",
            type = ActivityType.RUNNING,
            durationMinutes = 28,
            calories = 320,
            timestampMillis = 1711819800000
        ),
        ActivityUiModel(
            id = "a_3",
            name = "Cycling Session",
            type = ActivityType.CYCLING,
            durationMinutes = 45,
            calories = 410,
            timestampMillis = 1711906200000
        ),
        ActivityUiModel(
            id = "a_4",
            name = "Strength Training",
            type = ActivityType.STRENGTH,
            durationMinutes = 50,
            calories = 370,
            timestampMillis = 1711992600000
        ),
        ActivityUiModel(
            id = "a_5",
            name = "Yoga Flow",
            type = ActivityType.YOGA,
            durationMinutes = 30,
            calories = 160,
            timestampMillis = 1712079000000
        )
    )

    val foods: List<FoodUiModel> = listOf(
        FoodUiModel(
            id = "f_1",
            name = "Oatmeal Bowl",
            calories = 320,
            mealType = MealType.BREAKFAST,
            consumedAtMillis = 1711731600000,
            notes = "With banana and chia"
        ),
        FoodUiModel(
            id = "f_2",
            name = "Chicken Salad",
            calories = 450,
            mealType = MealType.LUNCH,
            consumedAtMillis = 1711816200000,
            notes = "Olive oil dressing"
        ),
        FoodUiModel(
            id = "f_3",
            name = "Fruit Smoothie",
            calories = 210,
            mealType = MealType.SNACKS,
            consumedAtMillis = 1711902600000,
            notes = "No added sugar"
        ),
        FoodUiModel(
            id = "f_4",
            name = "Grilled Fish with Rice",
            calories = 540,
            mealType = MealType.DINNER,
            consumedAtMillis = 1711989000000,
            notes = "Balanced dinner"
        )
    )
}

package com.aaditya.smartfit.data.di

import android.content.Context
import androidx.room.Room
import com.aaditya.smartfit.data.local.SmartFitDatabase
import com.aaditya.smartfit.data.preferences.UserPreferencesDataStore
import com.aaditya.smartfit.data.repository.ActivityRepository
import com.aaditya.smartfit.data.repository.AuthRepository
import com.aaditya.smartfit.data.repository.ProfileRepository
import com.aaditya.smartfit.data.repository.TipsRepository
import com.aaditya.smartfit.data.repository.impl.DataStoreProfileRepository
import com.aaditya.smartfit.data.repository.impl.LocalAuthRepository
import com.aaditya.smartfit.data.repository.impl.RemoteTipsRepository
import com.aaditya.smartfit.data.repository.impl.RoomActivityRepository

class AppContainer(
    context: Context
) {
    private val appContext = context.applicationContext

    private val database: SmartFitDatabase by lazy {
        Room.databaseBuilder(appContext, SmartFitDatabase::class.java, SMARTFIT_DATABASE)
            .fallbackToDestructiveMigration()
            .build()
    }

    val preferencesDataStore: UserPreferencesDataStore by lazy {
        UserPreferencesDataStore(appContext)
    }

    val activityRepository: ActivityRepository by lazy {
        RoomActivityRepository(
            activityDao = database.activityDao(),
            foodDao = database.foodDao()
        )
    }

    val authRepository: AuthRepository by lazy {
        LocalAuthRepository(
            userDao = database.userDao(),
            preferencesDataStore = preferencesDataStore
        )
    }

    val profileRepository: ProfileRepository by lazy {
        DataStoreProfileRepository(
            userDao = database.userDao(),
            preferencesDataStore = preferencesDataStore
        )
    }

    val tipsRepository: TipsRepository by lazy {
        RemoteTipsRepository()
    }

    private companion object {
        const val SMARTFIT_DATABASE = "smartfit_database"
    }
}


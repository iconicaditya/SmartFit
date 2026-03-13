package com.aaditya.smartfit

import android.app.Application
import com.aaditya.smartfit.data.local.SmartFitDatabase
import com.aaditya.smartfit.data.remote.RetrofitInstance
import com.aaditya.smartfit.data.repository.SmartFitRepository
import com.aaditya.smartfit.datastore.PreferenceManager

class SmartFitApplication : Application() {
    val database: SmartFitDatabase by lazy { SmartFitDatabase() }
    val repository: SmartFitRepository by lazy {
        SmartFitRepository(database.activityDao, RetrofitInstance.apiService)
    }
    val preferenceManager: PreferenceManager by lazy { PreferenceManager(this) }
}


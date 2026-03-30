package com.aaditya.smartfit.data.di

import android.content.Context

object SmartFitAppContainerProvider {
    @Volatile
    private var container: AppContainer? = null

    fun get(context: Context): AppContainer {
        return container ?: synchronized(this) {
            container ?: AppContainer(context).also { created ->
                container = created
            }
        }
    }
}


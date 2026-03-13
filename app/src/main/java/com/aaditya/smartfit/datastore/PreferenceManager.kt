package com.aaditya.smartfit.datastore

import android.content.Context

class PreferenceManager(context: Context) {
    private val prefs = context.getSharedPreferences("smartfit_prefs", Context.MODE_PRIVATE)

    fun setUserName(name: String) {
        prefs.edit().putString(KEY_USER_NAME, name).apply()
    }

    fun getUserName(): String = prefs.getString(KEY_USER_NAME, "Athlete") ?: "Athlete"

    companion object {
        private const val KEY_USER_NAME = "key_user_name"
    }
}


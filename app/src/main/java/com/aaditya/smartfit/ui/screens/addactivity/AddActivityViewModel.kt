package com.aaditya.smartfit.ui.screens.addactivity

class AddActivityViewModel {
    fun validateInput(name: String, durationMinutes: String): Boolean {
        return name.isNotBlank() && durationMinutes.toIntOrNull()?.let { it > 0 } == true
    }
}


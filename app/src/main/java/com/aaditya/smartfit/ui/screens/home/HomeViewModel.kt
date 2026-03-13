package com.aaditya.smartfit.ui.screens.home

import com.aaditya.smartfit.utils.DateUtils

class HomeViewModel {
    val userName: String = "Aaditya"
    val todaySteps: Int = 6840
    val stepGoal: Int = 10000

    fun stepProgress(): Float = DateUtils.calculateStepProgress(todaySteps, stepGoal)
}


package com.aaditya.smartfit.utils

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object DateUtils {
    fun formatDate(timestampMillis: Long): String {
        val formatter = SimpleDateFormat(Constants.DATE_PATTERN, Locale.getDefault())
        return formatter.format(Date(timestampMillis))
    }

    fun calculateStepProgress(steps: Int, goal: Int): Float {
        if (goal <= 0) return 0f
        return (steps.toFloat() / goal.toFloat()).coerceIn(0f, 1f)
    }
}


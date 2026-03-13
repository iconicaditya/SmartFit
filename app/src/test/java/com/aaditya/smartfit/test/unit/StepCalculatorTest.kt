package com.aaditya.smartfit.test.unit

import com.aaditya.smartfit.utils.DateUtils
import org.junit.Assert.assertEquals
import org.junit.Test

class StepCalculatorTest {
    @Test
    fun returnsExpectedStepProgress() {
        val result = DateUtils.calculateStepProgress(5000, 10000)
        assertEquals(0.5f, result, 0.0001f)
    }

    @Test
    fun returnsZeroWhenGoalIsInvalid() {
        val result = DateUtils.calculateStepProgress(5000, 0)
        assertEquals(0f, result, 0.0001f)
    }
}


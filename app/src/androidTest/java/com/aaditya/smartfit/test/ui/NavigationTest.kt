package com.aaditya.smartfit.test.ui

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import com.aaditya.smartfit.navigation.NavGraph
import org.junit.Rule
import org.junit.Test

class NavigationTest {
    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun startDestinationShowsWelcomeScreen() {
        composeRule.setContent {
            NavGraph()
        }

        composeRule.onNodeWithText("Welcome to SmartFit").assertIsDisplayed()
    }
}


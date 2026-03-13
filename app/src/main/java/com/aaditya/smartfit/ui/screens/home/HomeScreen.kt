package com.aaditya.smartfit.ui.screens.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.aaditya.smartfit.ui.components.ActivityCard
import com.aaditya.smartfit.ui.components.AnimatedButton
import com.aaditya.smartfit.ui.components.ProgressBarCard

@Composable
fun HomeScreen(
    onOpenActivityLog: () -> Unit,
    onOpenAddActivity: () -> Unit,
    onOpenProfile: () -> Unit
) {
    val viewModel = remember { HomeViewModel() }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        ProgressBarCard(
            title = "Today's Steps",
            progress = viewModel.stepProgress(),
            description = "${viewModel.todaySteps} / ${viewModel.stepGoal} steps"
        )
        ActivityCard(
            title = "Quick Activity",
            subtitle = "30 min brisk walk",
            value = "210 kcal"
        )
        AnimatedButton(text = "Open Activity Log", onClick = onOpenActivityLog)
        AnimatedButton(text = "Add Activity", onClick = onOpenAddActivity)
        AnimatedButton(text = "Profile", onClick = onOpenProfile)
    }
}


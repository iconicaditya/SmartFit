package com.aaditya.smartfit.ui.screens.profile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun ProfileScreen() {
    val viewModel = remember { ProfileViewModel() }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Text(text = "Profile", style = MaterialTheme.typography.headlineSmall)
        Text(text = "Name: ${viewModel.name}")
        Text(text = "Age: ${viewModel.age}")
        Text(text = "Weekly activity goal: ${viewModel.weeklyGoal} sessions")
    }
}


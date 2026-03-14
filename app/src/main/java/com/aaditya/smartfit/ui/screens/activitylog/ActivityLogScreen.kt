package com.aaditya.smartfit.ui.screens.activitylog

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
import com.aaditya.smartfit.ui.components.ActivityCard

@Composable
fun ActivityLogScreen() {
    val viewModel = remember { ActivityLogViewModel() }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Text(text = "Activity Log", style = MaterialTheme.typography.headlineSmall)
        viewModel.entries.forEachIndexed { index, item ->
            ActivityCard(
                title = item,
                subtitle = "Recorded activity #${index + 1}",
                value = "Done"
            )
        }
    }
}


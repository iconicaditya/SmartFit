package com.aaditya.smartfit.ui.screens.addactivity

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.aaditya.smartfit.ui.components.AnimatedButton

@Composable
fun AddActivityScreen(onBack: () -> Unit) {
    val viewModel = remember { AddActivityViewModel() }
    var activityName by remember { mutableStateOf("") }
    var duration by remember { mutableStateOf("") }
    var message by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Text(text = "Add Activity", style = MaterialTheme.typography.headlineSmall)
        OutlinedTextField(
            value = activityName,
            onValueChange = { activityName = it },
            label = { Text("Activity Name") },
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = duration,
            onValueChange = { duration = it },
            label = { Text("Duration (minutes)") },
            modifier = Modifier.fillMaxWidth()
        )
        AnimatedButton(
            text = "Save",
            onClick = {
                message = if (viewModel.validateInput(activityName, duration)) {
                    "Activity saved"
                } else {
                    "Please enter valid details"
                }
            }
        )
        if (message.isNotBlank()) {
            Text(text = message)
        }
        AnimatedButton(text = "Back to Home", onClick = onBack)
    }
}



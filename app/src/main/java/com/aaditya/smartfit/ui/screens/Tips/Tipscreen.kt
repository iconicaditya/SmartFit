package com.aaditya.smartfit.ui.screens.Tips

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Timelapse
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

private data class TipUiModel(
	val title: String,
	val description: String,
	val duration: String,
	val intensity: String,
	val calories: String
)

@Composable
fun TipsScreen() {
	val tips = remember {
		listOf(
			TipUiModel(
				title = "Warm Up First",
				description = "Start with 5-7 minutes of mobility drills to prevent injuries and improve performance.",
				duration = "6 min",
				intensity = "Easy",
				calories = "20 kcal"
			),
			TipUiModel(
				title = "Zone 2 Cardio",
				description = "Maintain a conversational pace for steady fat burn and better heart endurance.",
				duration = "30 min",
				intensity = "Moderate",
				calories = "220 kcal"
			),
			TipUiModel(
				title = "Progressive Overload",
				description = "Increase reps or resistance each week to keep muscle growth and strength improving.",
				duration = "15 min",
				intensity = "Medium",
				calories = "110 kcal"
			),
			TipUiModel(
				title = "Post Workout Nutrition",
				description = "Take protein and hydration within 45 minutes after training for better recovery.",
				duration = "5 min",
				intensity = "Low",
				calories = "-"
			)
		)
	}

	var query by remember { mutableStateOf("") }
	val filteredTips = remember(query, tips) {
		if (query.isBlank()) {
			tips
		} else {
			tips.filter {
				it.title.contains(query, ignoreCase = true) ||
					it.description.contains(query, ignoreCase = true)
			}
		}
	}

	Column(
		modifier = Modifier
			.fillMaxSize()
			.background(
				Brush.verticalGradient(
					colors = listOf(Color(0xFF0D1117), Color(0xFF0D1C1B), Color(0xFF0A1628))
				)
			)
			.padding(16.dp)
	) {
		Text(
			text = "Workout Tips",
			style = MaterialTheme.typography.headlineSmall,
			fontWeight = FontWeight.Bold,
			color = Color(0xFFF0F6FC)
		)
		Text(
			text = "Curated guidance to train smart, stay consistent, and recover better.",
			style = MaterialTheme.typography.bodyMedium,
			color = Color(0xFF8B949E),
			modifier = Modifier.padding(top = 4.dp)
		)

		Spacer(modifier = Modifier.height(14.dp))

		OutlinedTextField(
			value = query,
			onValueChange = { query = it },
			singleLine = true,
			modifier = Modifier.fillMaxWidth(),
			placeholder = { Text("Search tips...", color = Color(0xFF8B949E)) },
			shape = RoundedCornerShape(12.dp),
			colors = OutlinedTextFieldDefaults.colors(
				focusedBorderColor = Color(0xFF00C9A7),
				unfocusedBorderColor = Color(0xFF30363D),
				focusedContainerColor = Color(0xFF161B22),
				unfocusedContainerColor = Color(0xFF161B22),
				focusedTextColor = Color(0xFFF0F6FC),
				unfocusedTextColor = Color(0xFFF0F6FC),
				cursorColor = Color(0xFF00C9A7)
			)
		)

		Spacer(modifier = Modifier.height(14.dp))

		LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
			items(filteredTips) { tip ->
				TipCard(tip = tip)
			}
		}
	}
}

@Composable
private fun TipCard(tip: TipUiModel) {
	Card(
		shape = RoundedCornerShape(14.dp),
		colors = CardDefaults.cardColors(containerColor = Color(0xFF161B22)),
		modifier = Modifier.fillMaxWidth()
	) {
		Column(modifier = Modifier.padding(14.dp)) {
			Text(
				text = tip.title,
				style = MaterialTheme.typography.titleMedium,
				fontWeight = FontWeight.SemiBold,
				color = Color(0xFFF0F6FC)
			)
			Text(
				text = tip.description,
				style = MaterialTheme.typography.bodyMedium,
				color = Color(0xFFB6C2CF),
				modifier = Modifier.padding(top = 6.dp)
			)

			Spacer(modifier = Modifier.height(10.dp))

			Row(
				modifier = Modifier.fillMaxWidth(),
				horizontalArrangement = Arrangement.SpaceBetween,
				verticalAlignment = Alignment.CenterVertically
			) {
				TipMeta(icon = Icons.Filled.Timelapse, value = tip.duration)
				TipMeta(icon = Icons.Filled.Bolt, value = tip.intensity)
				TipMeta(icon = Icons.Filled.LocalFireDepartment, value = tip.calories)
			}
		}
	}
}

@Composable
private fun TipMeta(icon: androidx.compose.ui.graphics.vector.ImageVector, value: String) {
	Row(verticalAlignment = Alignment.CenterVertically) {
		Icon(imageVector = icon, contentDescription = null, tint = Color(0xFF00C9A7))
		Text(
			text = value,
			color = Color(0xFF8B949E),
			style = MaterialTheme.typography.bodySmall,
			modifier = Modifier.padding(start = 4.dp)
		)
	}
}


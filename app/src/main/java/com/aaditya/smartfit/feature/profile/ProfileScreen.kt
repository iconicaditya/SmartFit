package com.aaditya.smartfit.feature.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.DirectionsWalk
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.TrendingDown
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.aaditya.smartfit.R
import com.aaditya.smartfit.ui.theme.SmartFitTheme
import coil.compose.AsyncImage

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    uiState: ProfileUiState,
    modifier: Modifier = Modifier,
    onEvent: (ProfileUiEvent) -> Unit = {}
) {
    val scrollState = rememberScrollState()

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(id = R.string.profile_title),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { onEvent(ProfileUiEvent.BackClicked) }) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = stringResource(id = R.string.profile_back_content_description)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                ProfileHeroCard(
                    uiState = uiState,
                    onAvatarClick = { onEvent(ProfileUiEvent.AvatarClicked) }
                )

                AccountInfoCard(
                    name = uiState.name,
                    email = uiState.email,
                    onNameChanged = { onEvent(ProfileUiEvent.NameChanged(it)) },
                    onEmailChanged = { onEvent(ProfileUiEvent.EmailChanged(it)) }
                )

                PreferenceCard(
                    isDarkMode = uiState.isDarkMode,
                    notificationsEnabled = uiState.notificationsEnabled,
                    unitPreference = uiState.unitPreference,
                    onDarkModeToggled = { onEvent(ProfileUiEvent.DarkModeToggled(it)) },
                    onNotificationsToggled = { onEvent(ProfileUiEvent.NotificationsToggled(it)) },
                    onUnitChanged = { onEvent(ProfileUiEvent.UnitPreferenceChanged(it)) }
                )

                GoalCard(
                    stepGoalInput = uiState.stepGoalInput,
                    workoutGoalInput = uiState.workoutGoalInput,
                    calorieGoalInput = uiState.calorieGoalInput,
                    isSaving = uiState.isSaving,
                    onStepGoalChanged = { onEvent(ProfileUiEvent.StepGoalInputChanged(it)) },
                    onWorkoutGoalChanged = { onEvent(ProfileUiEvent.WorkoutGoalInputChanged(it)) },
                    onCalorieGoalChanged = { onEvent(ProfileUiEvent.CalorieGoalInputChanged(it)) }
                )

                if (uiState.errorMessage != null) {
                    FeedbackMessageCard(
                        message = uiState.errorMessage,
                        containerColor = MaterialTheme.colorScheme.errorContainer,
                        contentColor = MaterialTheme.colorScheme.onErrorContainer,
                        onDismiss = { onEvent(ProfileUiEvent.MessageDismissed) }
                    )
                }

                if (uiState.successMessage != null) {
                    FeedbackMessageCard(
                        message = uiState.successMessage,
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                        onDismiss = { onEvent(ProfileUiEvent.MessageDismissed) }
                    )
                }

                ActionSection(
                    isSaving = uiState.isSaving,
                    onSave = { onEvent(ProfileUiEvent.SaveClicked) },
                    onLogout = { onEvent(ProfileUiEvent.LogoutClicked) }
                )

                Spacer(modifier = Modifier.height(8.dp))
            }

            if (uiState.isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.scrim.copy(alpha = 0.18f)),
                    contentAlignment = Alignment.Center
                ) {
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 18.dp, vertical = 14.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.5.dp)
                            Text(
                                text = stringResource(id = R.string.profile_loading),
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ProfileHeroCard(
    uiState: ProfileUiState,
    onAvatarClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.horizontalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.16f),
                            MaterialTheme.colorScheme.secondary.copy(alpha = 0.12f)
                        )
                    )
                )
                .padding(horizontal = 16.dp, vertical = 18.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(82.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.9f)),
                contentAlignment = Alignment.Center
            ) {
                if (uiState.profileImageUri != null) {
                    AsyncImage(
                        model = uiState.profileImageUri,
                        contentDescription = stringResource(id = R.string.profile_avatar_content_description),
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Icon(
                        imageVector = Icons.Filled.AccountCircle,
                        contentDescription = stringResource(id = R.string.profile_avatar_content_description),
                        modifier = Modifier.size(66.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(onClick = onAvatarClick) {
                        Text(
                            text = stringResource(id = R.string.profile_edit_profile_action),
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.size(4.dp))
                        Icon(
                            imageVector = Icons.Outlined.Edit,
                            contentDescription = stringResource(id = R.string.profile_avatar_upload_content_description),
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
                Text(
                    text = uiState.name.ifBlank { stringResource(id = R.string.profile_default_name) },
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.ExtraBold
                )
                Text(
                    text = uiState.email,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun AccountInfoCard(
    name: String,
    email: String,
    onNameChanged: (String) -> Unit,
    onEmailChanged: (String) -> Unit
) {
    var isFirstNameEditable by rememberSaveable { mutableStateOf(false) }
    var isLastNameEditable by rememberSaveable { mutableStateOf(false) }
    var isEmailEditable by rememberSaveable { mutableStateOf(false) }

    val nameParts = name
        .trim()
        .split(Regex("\\s+"), limit = 2)
        .filter { it.isNotBlank() }
    val firstName = nameParts.firstOrNull().orEmpty()
    val lastName = nameParts.getOrNull(1).orEmpty()

    val mergeName: (String, String) -> String = { first, last ->
        listOf(first.trim(), last.trim())
            .filter { it.isNotEmpty() }
            .joinToString(" ")
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = stringResource(id = R.string.profile_account_section_title),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    value = firstName,
                    onValueChange = { updatedFirstName ->
                        if (isFirstNameEditable) {
                            onNameChanged(mergeName(updatedFirstName, lastName))
                        }
                    },
                    modifier = Modifier.weight(1f),
                    label = { Text(text = stringResource(id = R.string.profile_first_name_label)) },
                    readOnly = !isFirstNameEditable,
                    trailingIcon = {
                        EditFieldTrailingIcon(
                            label = stringResource(id = R.string.profile_first_name_label),
                            isEditable = isFirstNameEditable,
                            onEditClick = { isFirstNameEditable = true }
                        )
                    },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
                )

                OutlinedTextField(
                    value = lastName,
                    onValueChange = { updatedLastName ->
                        if (isLastNameEditable) {
                            onNameChanged(mergeName(firstName, updatedLastName))
                        }
                    },
                    modifier = Modifier.weight(1f),
                    label = { Text(text = stringResource(id = R.string.profile_last_name_label)) },
                    readOnly = !isLastNameEditable,
                    trailingIcon = {
                        EditFieldTrailingIcon(
                            label = stringResource(id = R.string.profile_last_name_label),
                            isEditable = isLastNameEditable,
                            onEditClick = { isLastNameEditable = true }
                        )
                    },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
                )
            }

            OutlinedTextField(
                value = email,
                onValueChange = { updatedEmail ->
                    if (isEmailEditable) {
                        onEmailChanged(updatedEmail)
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                label = { Text(text = stringResource(id = R.string.profile_email_label)) },
                readOnly = !isEmailEditable,
                trailingIcon = {
                    EditFieldTrailingIcon(
                        label = stringResource(id = R.string.profile_email_label),
                        isEditable = isEmailEditable,
                        onEditClick = { isEmailEditable = true }
                    )
                },
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email,
                    imeAction = ImeAction.Done
                )
            )
        }
    }
}

@Composable
private fun PreferenceCard(
    isDarkMode: Boolean,
    notificationsEnabled: Boolean,
    unitPreference: UnitPreference,
    onDarkModeToggled: (Boolean) -> Unit,
    onNotificationsToggled: (Boolean) -> Unit,
    onUnitChanged: (UnitPreference) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Text(
                text = stringResource(id = R.string.profile_preferences_section_title),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )

            PreferenceToggleRow(
                icon = {
                    Icon(
                        imageVector = Icons.Filled.WbSunny,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                },
                title = stringResource(id = R.string.profile_dark_mode_label),
                subtitle = stringResource(id = R.string.profile_dark_mode_support),
                checked = isDarkMode,
                onCheckedChange = onDarkModeToggled
            )

            PreferenceToggleRow(
                icon = {
                    Icon(
                        imageVector = Icons.Filled.Notifications,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                },
                title = stringResource(id = R.string.profile_notifications_label),
                subtitle = stringResource(id = R.string.profile_notifications_support),
                checked = notificationsEnabled,
                onCheckedChange = onNotificationsToggled
            )

            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Filled.DirectionsWalk,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = stringResource(id = R.string.profile_units_title),
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Medium
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    UnitPreferenceChip(
                        text = stringResource(id = R.string.profile_units_steps),
                        selected = unitPreference == UnitPreference.STEPS,
                        onClick = { onUnitChanged(UnitPreference.STEPS) }
                    )
                    UnitPreferenceChip(
                        text = stringResource(id = R.string.profile_units_km),
                        selected = unitPreference == UnitPreference.KILOMETERS,
                        onClick = { onUnitChanged(UnitPreference.KILOMETERS) }
                    )
                }
            }
        }
    }
}

@Composable
private fun PreferenceToggleRow(
    icon: @Composable () -> Unit,
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            modifier = Modifier.weight(1f),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            icon()
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange
        )
    }
}

@Composable
private fun UnitPreferenceChip(
    text: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    AssistChip(
        onClick = onClick,
        label = {
            Text(
                text = text,
                fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal
            )
        },
        colors = AssistChipDefaults.assistChipColors(
            containerColor = if (selected) {
                MaterialTheme.colorScheme.primary.copy(alpha = 0.18f)
            } else {
                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
            }
        )
    )
}

@Composable
private fun GoalCard(
    stepGoalInput: String,
    workoutGoalInput: String,
    calorieGoalInput: String,
    isSaving: Boolean,
    onStepGoalChanged: (String) -> Unit,
    onWorkoutGoalChanged: (String) -> Unit,
    onCalorieGoalChanged: (String) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = stringResource(id = R.string.profile_goal_section_title),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )

            Text(
                text = stringResource(id = R.string.profile_goal_section_subtitle),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            EditableGoalRow(
                icon = Icons.Filled.DirectionsWalk,
                title = stringResource(id = R.string.profile_daily_step_goal_label),
                unit = stringResource(id = R.string.profile_goal_unit_steps_day),
                value = stepGoalInput,
                onValueChange = onStepGoalChanged
            )

            EditableGoalRow(
                icon = Icons.Filled.FitnessCenter,
                title = stringResource(id = R.string.profile_daily_workout_goal_label),
                unit = stringResource(id = R.string.profile_goal_unit_minutes_day),
                value = workoutGoalInput,
                onValueChange = onWorkoutGoalChanged
            )

            EditableGoalRow(
                icon = Icons.Filled.LocalFireDepartment,
                title = stringResource(id = R.string.profile_daily_calorie_goal_label),
                unit = stringResource(id = R.string.profile_goal_unit_kcal_day),
                value = calorieGoalInput,
                onValueChange = onCalorieGoalChanged
            )

            HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.22f))

            Text(
                text = stringResource(id = R.string.profile_recommendations_section_title),
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold
            )

            Text(
                text = stringResource(id = R.string.profile_recommendations_section_subtitle),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            RecommendationCard(
                title = stringResource(id = R.string.profile_weight_loss_title),
                icon = Icons.Filled.TrendingDown,
                stepsValue = stringResource(id = R.string.profile_weight_loss_steps_value),
                workoutValue = stringResource(id = R.string.profile_weight_loss_workout_value),
                caloriesValue = stringResource(id = R.string.profile_weight_loss_calories_value)
            )

            RecommendationCard(
                title = stringResource(id = R.string.profile_weight_gain_title),
                icon = Icons.Filled.TrendingUp,
                stepsValue = stringResource(id = R.string.profile_weight_gain_steps_value),
                workoutValue = stringResource(id = R.string.profile_weight_gain_workout_value),
                caloriesValue = stringResource(id = R.string.profile_weight_gain_calories_value)
            )

            if (isSaving) {
                LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
            }
        }
    }
}

@Composable
private fun EditableGoalRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    unit: String,
    value: String,
    onValueChange: (String) -> Unit
) {
    var isEditable by rememberSaveable(title) { mutableStateOf(false) }

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium
            )
        }

        OutlinedTextField(
            value = value,
            onValueChange = { input ->
                if (isEditable) {
                    onValueChange(input.filter { it.isDigit() }.take(5))
                }
            },
            singleLine = true,
            readOnly = !isEditable,
            trailingIcon = {
                EditFieldTrailingIcon(
                    label = title,
                    isEditable = isEditable,
                    onEditClick = { isEditable = true }
                )
            },
            supportingText = { Text(text = unit) },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Done
            ),
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
private fun EditFieldTrailingIcon(
    label: String,
    isEditable: Boolean,
    onEditClick: () -> Unit
) {
    IconButton(onClick = onEditClick) {
        Icon(
            imageVector = Icons.Outlined.Edit,
            contentDescription = stringResource(
                id = R.string.profile_edit_field_content_description,
                label
            ),
            tint = if (isEditable) {
                MaterialTheme.colorScheme.primary
            } else {
                MaterialTheme.colorScheme.onSurfaceVariant
            }
        )
    }
}

@Composable
private fun RecommendationCard(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    stepsValue: String,
    workoutValue: String,
    caloriesValue: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold
                )
            }

            RecommendationMetricRow(
                label = stringResource(id = R.string.profile_recommendation_steps_label),
                value = stepsValue
            )
            RecommendationMetricRow(
                label = stringResource(id = R.string.profile_recommendation_workout_label),
                value = workoutValue
            )
            RecommendationMetricRow(
                label = stringResource(id = R.string.profile_recommendation_calorie_label),
                value = caloriesValue
            )
        }
    }
}

@Composable
private fun RecommendationMetricRow(
    label: String,
    value: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
private fun FeedbackMessageCard(
    message: String,
    containerColor: Color,
    contentColor: Color,
    onDismiss: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = containerColor)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium,
                color = contentColor,
                textAlign = TextAlign.Start
            )
            TextButton(
                onClick = onDismiss,
                modifier = Modifier.align(Alignment.End)
            ) {
                Text(
                    text = stringResource(id = R.string.profile_dismiss),
                    color = contentColor
                )
            }
        }
    }
}

@Composable
private fun ActionSection(
    isSaving: Boolean,
    onSave: () -> Unit,
    onLogout: () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Button(
            onClick = onSave,
            enabled = !isSaving,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = RoundedCornerShape(14.dp)
        ) {
            Text(
                text = if (isSaving) {
                    stringResource(id = R.string.profile_saving)
                } else {
                    stringResource(id = R.string.profile_save_button)
                },
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold
            )
        }

        OutlinedButton(
            onClick = onLogout,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = RoundedCornerShape(14.dp)
        ) {
            Text(
                text = stringResource(id = R.string.profile_logout_button),
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ProfileScreenPreview() {
    SmartFitTheme(dynamicColor = false) {
        ProfileScreen(
            uiState = ProfileUiState(),
            onEvent = {}
        )
    }
}

package com.aaditya.smartfit.feature.activities.form

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.aaditya.smartfit.R
import com.aaditya.smartfit.feature.activities.list.ActivityType
import com.aaditya.smartfit.ui.theme.SmartFitGreen
import com.aaditya.smartfit.ui.theme.SmartFitTheme
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ActivityFormScreen(
    uiState: ActivityFormUiState,
    onEvent: (ActivityFormUiEvent) -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var isTypeMenuExpanded by rememberSaveable { mutableStateOf(false) }

    val context = LocalContext.current

    fun openDatePicker() {
        val calendar = Calendar.getInstance()
        DatePickerDialog(
            context,
            { _, year, month, dayOfMonth ->
                val selectedDateText = String.format(
                    Locale.getDefault(),
                    "%04d-%02d-%02d",
                    year,
                    month + 1,
                    dayOfMonth
                )
                onEvent(ActivityFormUiEvent.OnDateChanged(selectedDateText))
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    fun openTimePicker() {
        val calendar = Calendar.getInstance()
        TimePickerDialog(
            context,
            { _, hourOfDay, minute ->
                val selectedTime = Calendar.getInstance().apply {
                    set(Calendar.HOUR_OF_DAY, hourOfDay)
                    set(Calendar.MINUTE, minute)
                }
                val timeText = SimpleDateFormat("hh:mm a", Locale.getDefault()).format(selectedTime.time)
                onEvent(ActivityFormUiEvent.OnTimeChanged(timeText))
            },
            calendar.get(Calendar.HOUR_OF_DAY),
            calendar.get(Calendar.MINUTE),
            false
        ).show()
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.primary.copy(alpha = 0.08f),
                        MaterialTheme.colorScheme.background
                    )
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .padding(horizontal = 20.dp, vertical = 16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            TextButton(
                onClick = onBackClick,
                modifier = Modifier.align(Alignment.Start)
            ) {
                Text(
                    text = stringResource(id = R.string.activity_form_back),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            AnimatedVisibility(
                visible = true,
                enter = fadeIn(animationSpec = tween(450)) + slideInVertically(
                    initialOffsetY = { it / 4 },
                    animationSpec = tween(450)
                )
            ) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(22.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.96f)
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 18.dp, vertical = 18.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = if (uiState.isEditMode) {
                                stringResource(id = R.string.activity_form_edit_title)
                            } else {
                                stringResource(id = R.string.activity_form_add_title)
                            },
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.ExtraBold
                        )
                        Text(
                            text = stringResource(id = R.string.activity_form_subtitle),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.97f)
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 5.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    FormLabel(text = stringResource(id = R.string.activity_form_name_label))
                    OutlinedTextField(
                        value = uiState.activityName,
                        onValueChange = {
                            onEvent(ActivityFormUiEvent.OnNameChanged(it))
                        },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = {
                            Text(text = stringResource(id = R.string.activity_form_name_placeholder))
                        },
                        singleLine = true,
                        isError = uiState.nameError != null,
                        colors = outlinedColors()
                    )
                    InlineError(message = uiState.nameError)

                    FormLabel(text = stringResource(id = R.string.activity_form_type_label))
                    ExposedDropdownMenuBox(
                        expanded = isTypeMenuExpanded,
                        onExpandedChange = { isTypeMenuExpanded = !isTypeMenuExpanded }
                    ) {
                        OutlinedTextField(
                            value = uiState.activityType.name.lowercase().replaceFirstChar { it.uppercase() },
                            onValueChange = {},
                            readOnly = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor(),
                            trailingIcon = {
                                ExposedDropdownMenuDefaults.TrailingIcon(expanded = isTypeMenuExpanded)
                            },
                            colors = outlinedColors()
                        )
                        ExposedDropdownMenu(
                            expanded = isTypeMenuExpanded,
                            onDismissRequest = { isTypeMenuExpanded = false }
                        ) {
                            ActivityType.entries.forEach { type ->
                                DropdownMenuItem(
                                    text = {
                                        Text(text = type.name.lowercase().replaceFirstChar { it.uppercase() })
                                    },
                                    onClick = {
                                        onEvent(ActivityFormUiEvent.OnTypeChanged(type))
                                        isTypeMenuExpanded = false
                                    }
                                )
                            }
                        }
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        Column(modifier = Modifier.weight(1f)) {
                            FormLabel(text = stringResource(id = R.string.activity_form_duration_label))
                            OutlinedTextField(
                                value = uiState.durationMinutes,
                                onValueChange = {
                                    onEvent(ActivityFormUiEvent.OnDurationChanged(it))
                                },
                                modifier = Modifier.fillMaxWidth(),
                                placeholder = {
                                    Text(text = stringResource(id = R.string.activity_form_duration_placeholder))
                                },
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                isError = uiState.durationError != null,
                                colors = outlinedColors()
                            )
                            InlineError(message = uiState.durationError)
                        }
                        Column(modifier = Modifier.weight(1f)) {
                            FormLabel(text = stringResource(id = R.string.activity_form_calories_label))
                            OutlinedTextField(
                                value = uiState.calories,
                                onValueChange = {
                                    onEvent(ActivityFormUiEvent.OnCaloriesChanged(it))
                                },
                                modifier = Modifier.fillMaxWidth(),
                                placeholder = {
                                    Text(text = stringResource(id = R.string.activity_form_calories_placeholder))
                                },
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                isError = uiState.caloriesError != null,
                                colors = outlinedColors()
                            )
                            InlineError(message = uiState.caloriesError)
                        }
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        Column(modifier = Modifier.weight(1f)) {
                            FormLabel(text = stringResource(id = R.string.activity_form_date_label))
                            OutlinedTextField(
                                value = uiState.dateLabel,
                                onValueChange = {},
                                readOnly = true,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable(onClick = ::openDatePicker),
                                placeholder = {
                                    Text(text = stringResource(id = R.string.activity_form_date_placeholder))
                                },
                                trailingIcon = {
                                    IconButton(onClick = ::openDatePicker) {
                                        Icon(
                                            imageVector = Icons.Outlined.CalendarMonth,
                                            contentDescription = stringResource(
                                                id = R.string.activity_form_pick_date_content_description
                                            )
                                        )
                                    }
                                },
                                singleLine = true,
                                isError = uiState.dateError != null,
                                colors = outlinedColors()
                            )
                            InlineError(message = uiState.dateError)
                        }
                        Column(modifier = Modifier.weight(1f)) {
                            FormLabel(text = stringResource(id = R.string.activity_form_time_label))
                            OutlinedTextField(
                                value = uiState.timeLabel,
                                onValueChange = {},
                                readOnly = true,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable(onClick = ::openTimePicker),
                                placeholder = {
                                    Text(text = stringResource(id = R.string.activity_form_time_placeholder))
                                },
                                trailingIcon = {
                                    IconButton(onClick = ::openTimePicker) {
                                        Icon(
                                            imageVector = Icons.Outlined.Schedule,
                                            contentDescription = stringResource(
                                                id = R.string.activity_form_pick_time_content_description
                                            )
                                        )
                                    }
                                },
                                singleLine = true,
                                isError = uiState.timeError != null,
                                colors = outlinedColors()
                            )
                            InlineError(message = uiState.timeError)
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Button(
                        onClick = { onEvent(ActivityFormUiEvent.OnSubmitClicked) },
                        enabled = !uiState.isSaving,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(54.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = SmartFitGreen)
                    ) {
                        if (uiState.isSaving) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                strokeWidth = 2.dp,
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = stringResource(id = R.string.activity_form_saving),
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                        } else {
                            Text(
                                text = if (uiState.isEditMode) {
                                    stringResource(id = R.string.activity_form_update_button)
                                } else {
                                    stringResource(id = R.string.activity_form_save_button)
                                },
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                        }
                    }

                    AnimatedVisibility(visible = uiState.globalMessage != null) {
                        Text(
                            text = uiState.globalMessage.orEmpty(),
                            style = MaterialTheme.typography.bodySmall,
                            color = if (uiState.isSuccessMessage) SmartFitGreen else MaterialTheme.colorScheme.error,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun FormLabel(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.labelLarge,
        color = MaterialTheme.colorScheme.onSurface,
        fontWeight = FontWeight.SemiBold
    )
}

@Composable
private fun InlineError(message: String?) {
    if (message == null) return
    Text(
        text = message,
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.error
    )
}

@Composable
private fun outlinedColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor = SmartFitGreen,
    focusedLabelColor = SmartFitGreen
)

@Preview(showBackground = true)
@Composable
private fun ActivityFormScreenPreview() {
    SmartFitTheme(dynamicColor = false) {
        ActivityFormScreen(
            uiState = ActivityFormUiState(),
            onEvent = {},
            onBackClick = {}
        )
    }
}

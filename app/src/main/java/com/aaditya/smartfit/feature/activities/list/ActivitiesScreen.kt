package com.aaditya.smartfit.feature.activities.list

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material.icons.outlined.Sort
import androidx.compose.material.icons.outlined.WarningAmber
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.aaditya.smartfit.R
import com.aaditya.smartfit.core.ui.components.ActivityCard
import com.aaditya.smartfit.core.ui.components.FoodCard
import com.aaditya.smartfit.ui.theme.SmartFitBlue
import com.aaditya.smartfit.ui.theme.SmartFitGreen
import com.aaditya.smartfit.ui.theme.SmartFitTheme
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ActivitiesScreen(
    uiState: ActivitiesScreenState,
    onTabChanged: (ActivitiesTab) -> Unit,
    onActivityFilterChanged: (ActivityFilterOption) -> Unit,
    onActivitySortChanged: (ActivitySortOption) -> Unit,
    onFoodFilterChanged: (FoodFilterOption) -> Unit,
    onFoodSortChanged: (FoodSortOption) -> Unit,
    onRefreshClick: () -> Unit,
    onSortMenuExpandedChanged: (Boolean) -> Unit,
    onRequestDeleteActivity: (String) -> Unit,
    onRequestDeleteFood: (String) -> Unit,
    onDismissDeleteDialogs: () -> Unit,
    onConfirmDeleteActivity: () -> Unit,
    onConfirmDeleteFood: () -> Unit,
    onUndoDeleteActivity: () -> Unit,
    onUndoDeleteFood: () -> Unit,
    onConsumeDeletedActivityUndo: () -> Unit,
    onConsumeDeletedFoodUndo: () -> Unit,
    onAddActivityClick: () -> Unit,
    onEditActivityClick: (String) -> Unit,
    onAddFoodClick: () -> Unit,
    onEditFoodClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    val activityDeletedText = stringResource(id = R.string.activities_deleted_feedback)
    val foodDeletedText = stringResource(id = R.string.food_deleted_feedback)
    val undoText = stringResource(id = R.string.common_undo)

    val allActivities = uiState.allActivities
    val allFoods = uiState.allFoods
    val selectedTab = uiState.selectedTab
    val activityFilterOption = uiState.activityFilterOption
    val activitySortOption = uiState.activitySortOption
    val foodFilterOption = uiState.foodFilterOption
    val foodSortOption = uiState.foodSortOption
    val deleteActivityTargetId = uiState.deleteActivityTargetId
    val deleteFoodTargetId = uiState.deleteFoodTargetId
    val sortMenuExpanded = uiState.sortMenuExpanded

    val activityItems by remember(allActivities, activityFilterOption, activitySortOption) {
        derivedStateOf {
            val nowMillis = System.currentTimeMillis()
            val oneDayMillis = 24L * 60L * 60L * 1000L
            val oneWeekMillis = 7L * oneDayMillis

            val filtered = allActivities.filter { item ->
                val delta = nowMillis - item.timestampMillis
                when (activityFilterOption) {
                    ActivityFilterOption.TODAY -> delta in 0..oneDayMillis
                    ActivityFilterOption.WEEKLY -> delta in 0..oneWeekMillis
                    ActivityFilterOption.ALL_TIME -> true
                }
            }

            when (activitySortOption) {
                ActivitySortOption.NEWEST -> filtered.sortedByDescending { it.timestampMillis }
                ActivitySortOption.CALORIES_HIGH -> filtered.sortedByDescending { it.calories }
                ActivitySortOption.DURATION_LONG -> filtered.sortedByDescending { it.durationMinutes }
            }
        }
    }

    val foodItems by remember(allFoods, foodFilterOption, foodSortOption) {
        derivedStateOf {
            val filtered = allFoods.filter { item ->
                when (foodFilterOption) {
                    FoodFilterOption.ALL_MEALS -> true
                    FoodFilterOption.BREAKFAST -> item.mealType == MealType.BREAKFAST
                    FoodFilterOption.LUNCH -> item.mealType == MealType.LUNCH
                    FoodFilterOption.DINNER -> item.mealType == MealType.DINNER
                    FoodFilterOption.SNACKS -> item.mealType == MealType.SNACKS
                }
            }

            when (foodSortOption) {
                FoodSortOption.NEWEST -> filtered.sortedByDescending { it.consumedAtMillis }
                FoodSortOption.CALORIES_HIGH -> filtered.sortedByDescending { it.calories }
                FoodSortOption.CALORIES_LOW -> filtered.sortedBy { it.calories }
            }
        }
    }

    val caloriesBurnedToday by remember(allActivities) {
        derivedStateOf {
            val nowMillis = System.currentTimeMillis()
            val oneDayMillis = 24L * 60L * 60L * 1000L
            allActivities
                .filter { item ->
                    val delta = nowMillis - item.timestampMillis
                    delta in 0..oneDayMillis
                }
                .sumOf { it.calories }
        }
    }

    val caloriesConsumedToday by remember(allFoods) {
        derivedStateOf {
            val nowMillis = System.currentTimeMillis()
            val oneDayMillis = 24L * 60L * 60L * 1000L
            allFoods
                .filter { item ->
                    val delta = nowMillis - item.consumedAtMillis
                    delta in 0..oneDayMillis
                }
                .sumOf { it.calories }
        }
    }

    LaunchedEffect(selectedTab) {
        onSortMenuExpandedChanged(false)
    }

    LaunchedEffect(uiState.lastDeletedActivity?.id) {
        if (uiState.lastDeletedActivity != null) {
            val result = snackbarHostState.showSnackbar(
                message = activityDeletedText,
                actionLabel = undoText,
                withDismissAction = true
            )
            if (result == SnackbarResult.ActionPerformed) {
                onUndoDeleteActivity()
            }
            onConsumeDeletedActivityUndo()
        }
    }

    LaunchedEffect(uiState.lastDeletedFood?.id) {
        if (uiState.lastDeletedFood != null) {
            val result = snackbarHostState.showSnackbar(
                message = foodDeletedText,
                actionLabel = undoText,
                withDismissAction = true
            )
            if (result == SnackbarResult.ActionPerformed) {
                onUndoDeleteFood()
            }
            onConsumeDeletedFoodUndo()
        }
    }

    deleteActivityTargetId?.let { activityId ->
        AlertDialog(
            onDismissRequest = onDismissDeleteDialogs,
            confirmButton = {
                TextButton(
                    onClick = {
                        onConfirmDeleteActivity()
                    }
                ) {
                    Text(text = stringResource(id = R.string.activities_delete_confirm_yes))
                }
            },
            dismissButton = {
                TextButton(onClick = onDismissDeleteDialogs) {
                    Text(text = stringResource(id = R.string.activities_delete_confirm_cancel))
                }
            },
            containerColor = MaterialTheme.colorScheme.surface,
            titleContentColor = MaterialTheme.colorScheme.onSurface,
            textContentColor = MaterialTheme.colorScheme.onSurfaceVariant,
            icon = {
                Icon(
                    imageVector = Icons.Outlined.WarningAmber,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.error
                )
            },
            title = {
                Text(text = stringResource(id = R.string.activities_delete_confirm_title))
            },
            text = {
                Text(text = stringResource(id = R.string.activities_delete_confirm_message))
            }
        )
    }

    deleteFoodTargetId?.let { foodId ->
        AlertDialog(
            onDismissRequest = onDismissDeleteDialogs,
            confirmButton = {
                TextButton(
                    onClick = {
                        onConfirmDeleteFood()
                    }
                ) {
                    Text(text = stringResource(id = R.string.food_delete_confirm_yes))
                }
            },
            dismissButton = {
                TextButton(onClick = onDismissDeleteDialogs) {
                    Text(text = stringResource(id = R.string.food_delete_confirm_cancel))
                }
            },
            containerColor = MaterialTheme.colorScheme.surface,
            titleContentColor = MaterialTheme.colorScheme.onSurface,
            textContentColor = MaterialTheme.colorScheme.onSurfaceVariant,
            icon = {
                Icon(
                    imageVector = Icons.Outlined.WarningAmber,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.error
                )
            },
            title = {
                Text(text = stringResource(id = R.string.food_delete_confirm_title))
            },
            text = {
                Text(text = stringResource(id = R.string.food_delete_confirm_message))
            }
        )
    }

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = stringResource(id = R.string.activities_title),
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = stringResource(id = R.string.activities_subtitle),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                actions = {
                    IconButton(onClick = onRefreshClick) {
                        Icon(
                            imageVector = Icons.Outlined.Refresh,
                            contentDescription = stringResource(id = R.string.activities_refresh_content_description)
                        )
                    }
                    Box {
                        IconButton(onClick = { onSortMenuExpandedChanged(true) }) {
                            Icon(
                                imageVector = Icons.Outlined.Sort,
                                contentDescription = stringResource(id = R.string.activities_sort_content_description)
                            )
                        }
                        DropdownMenu(
                            expanded = sortMenuExpanded,
                            onDismissRequest = { onSortMenuExpandedChanged(false) }
                        ) {
                            if (selectedTab == ActivitiesTab.ACTIVITIES) {
                                DropdownMenuItem(
                                    text = { Text(text = stringResource(id = R.string.activities_sort_newest)) },
                                    onClick = {
                                        onActivitySortChanged(ActivitySortOption.NEWEST)
                                    }
                                )
                                DropdownMenuItem(
                                    text = { Text(text = stringResource(id = R.string.activities_sort_calories)) },
                                    onClick = {
                                        onActivitySortChanged(ActivitySortOption.CALORIES_HIGH)
                                    }
                                )
                                DropdownMenuItem(
                                    text = { Text(text = stringResource(id = R.string.activities_sort_duration)) },
                                    onClick = {
                                        onActivitySortChanged(ActivitySortOption.DURATION_LONG)
                                    }
                                )
                            } else {
                                DropdownMenuItem(
                                    text = { Text(text = stringResource(id = R.string.food_sort_newest)) },
                                    onClick = {
                                        onFoodSortChanged(FoodSortOption.NEWEST)
                                    }
                                )
                                DropdownMenuItem(
                                    text = { Text(text = stringResource(id = R.string.food_sort_calories_high)) },
                                    onClick = {
                                        onFoodSortChanged(FoodSortOption.CALORIES_HIGH)
                                    }
                                )
                                DropdownMenuItem(
                                    text = { Text(text = stringResource(id = R.string.food_sort_calories_low)) },
                                    onClick = {
                                        onFoodSortChanged(FoodSortOption.CALORIES_LOW)
                                    }
                                )
                            }
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    if (selectedTab == ActivitiesTab.ACTIVITIES) {
                        onAddActivityClick()
                    } else {
                        onAddFoodClick()
                    }
                },
                containerColor = SmartFitGreen,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ) {
                Icon(
                    imageVector = Icons.Outlined.Add,
                    contentDescription = if (selectedTab == ActivitiesTab.ACTIVITIES) {
                        stringResource(id = R.string.activities_add_content_description)
                    } else {
                        stringResource(id = R.string.food_add_content_description)
                    }
                )
            }
        },
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            SmartFitBlue.copy(alpha = 0.06f),
                            MaterialTheme.colorScheme.background
                        )
                    )
                )
        ) {
            DailySummarySection(
                consumedCalories = caloriesConsumedToday,
                burnedCalories = caloriesBurnedToday
            )

            TabRow(selectedTabIndex = selectedTab.ordinal) {
                Tab(
                    selected = selectedTab == ActivitiesTab.ACTIVITIES,
                    onClick = { onTabChanged(ActivitiesTab.ACTIVITIES) },
                    text = { Text(text = stringResource(id = R.string.activities_tab_activities)) }
                )
                Tab(
                    selected = selectedTab == ActivitiesTab.FOOD,
                    onClick = { onTabChanged(ActivitiesTab.FOOD) },
                    text = { Text(text = stringResource(id = R.string.activities_tab_food)) }
                )
            }

            if (selectedTab == ActivitiesTab.ACTIVITIES) {
                ActivityFilterSection(
                    selected = activityFilterOption,
                    onFilterSelected = onActivityFilterChanged
                )

                HorizontalDivider(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    color = MaterialTheme.colorScheme.outline.copy(alpha = 0.22f)
                )

                if (activityItems.isEmpty()) {
                    EmptyStateView(
                        title = stringResource(id = R.string.activities_empty_title),
                        message = stringResource(id = R.string.activities_empty_message),
                        actionLabel = stringResource(id = R.string.activities_empty_cta),
                        onAddClick = onAddActivityClick
                    )
                } else {
                    ActivityList(
                        items = activityItems,
                        onEditActivityClick = onEditActivityClick,
                        onDeleteRequested = onRequestDeleteActivity
                    )
                }
            } else {
                FoodFilterSection(
                    selected = foodFilterOption,
                    onFilterSelected = onFoodFilterChanged
                )

                HorizontalDivider(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    color = MaterialTheme.colorScheme.outline.copy(alpha = 0.22f)
                )

                if (foodItems.isEmpty()) {
                    EmptyStateView(
                        title = stringResource(id = R.string.food_empty_title),
                        message = stringResource(id = R.string.food_empty_message),
                        actionLabel = stringResource(id = R.string.food_empty_cta),
                        onAddClick = onAddFoodClick
                    )
                } else {
                    FoodList(
                        items = foodItems,
                        onEditFoodClick = onEditFoodClick,
                        onDeleteRequested = onRequestDeleteFood
                    )
                }
            }
        }
    }
}

@Composable
private fun DailySummarySection(
    consumedCalories: Int,
    burnedCalories: Int
) {
    val netCalories = consumedCalories - burnedCalories
    val netColor = if (netCalories <= 0) SmartFitGreen else MaterialTheme.colorScheme.error

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.97f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(
                text = stringResource(id = R.string.activities_summary_title),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                SummaryItem(
                    label = stringResource(id = R.string.activities_summary_consumed),
                    value = stringResource(id = R.string.activities_summary_kcal_value, consumedCalories),
                    valueColor = MaterialTheme.colorScheme.onSurface
                )
                SummaryItem(
                    label = stringResource(id = R.string.activities_summary_burned),
                    value = stringResource(id = R.string.activities_summary_kcal_value, burnedCalories),
                    valueColor = SmartFitBlue
                )
                SummaryItem(
                    label = stringResource(id = R.string.activities_summary_net),
                    value = stringResource(id = R.string.activities_summary_signed_kcal_value, netCalories),
                    valueColor = netColor
                )
            }
        }
    }
}

@Composable
private fun SummaryItem(
    label: String,
    value: String,
    valueColor: androidx.compose.ui.graphics.Color
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            color = valueColor
        )
    }
}

@Composable
private fun ActivityFilterSection(
    selected: ActivityFilterOption,
    onFilterSelected: (ActivityFilterOption) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Text(
            text = stringResource(id = R.string.activities_filter_title),
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontWeight = FontWeight.SemiBold
        )
        Spacer(modifier = Modifier.height(8.dp))
        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            item {
                FilterChip(
                    text = stringResource(id = R.string.activities_filter_all_time),
                    selected = selected == ActivityFilterOption.ALL_TIME,
                    onClick = { onFilterSelected(ActivityFilterOption.ALL_TIME) }
                )
            }
            item {
                FilterChip(
                    text = stringResource(id = R.string.activities_filter_today),
                    selected = selected == ActivityFilterOption.TODAY,
                    onClick = { onFilterSelected(ActivityFilterOption.TODAY) }
                )
            }
            item {
                FilterChip(
                    text = stringResource(id = R.string.activities_filter_weekly),
                    selected = selected == ActivityFilterOption.WEEKLY,
                    onClick = { onFilterSelected(ActivityFilterOption.WEEKLY) }
                )
            }
        }
    }
}

@Composable
private fun FoodFilterSection(
    selected: FoodFilterOption,
    onFilterSelected: (FoodFilterOption) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Text(
            text = stringResource(id = R.string.food_filter_title),
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontWeight = FontWeight.SemiBold
        )
        Spacer(modifier = Modifier.height(8.dp))
        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            item {
                FilterChip(
                    text = stringResource(id = R.string.food_filter_all),
                    selected = selected == FoodFilterOption.ALL_MEALS,
                    onClick = { onFilterSelected(FoodFilterOption.ALL_MEALS) }
                )
            }
            item {
                FilterChip(
                    text = stringResource(id = R.string.food_filter_breakfast),
                    selected = selected == FoodFilterOption.BREAKFAST,
                    onClick = { onFilterSelected(FoodFilterOption.BREAKFAST) }
                )
            }
            item {
                FilterChip(
                    text = stringResource(id = R.string.food_filter_lunch),
                    selected = selected == FoodFilterOption.LUNCH,
                    onClick = { onFilterSelected(FoodFilterOption.LUNCH) }
                )
            }
            item {
                FilterChip(
                    text = stringResource(id = R.string.food_filter_dinner),
                    selected = selected == FoodFilterOption.DINNER,
                    onClick = { onFilterSelected(FoodFilterOption.DINNER) }
                )
            }
            item {
                FilterChip(
                    text = stringResource(id = R.string.food_filter_snacks),
                    selected = selected == FoodFilterOption.SNACKS,
                    onClick = { onFilterSelected(FoodFilterOption.SNACKS) }
                )
            }
        }
    }
}

@Composable
private fun FilterChip(
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
            containerColor = if (selected) SmartFitBlue.copy(alpha = 0.16f) else MaterialTheme.colorScheme.surface,
            labelColor = if (selected) SmartFitBlue else MaterialTheme.colorScheme.onSurfaceVariant
        )
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ActivityList(
    items: List<ActivityUiModel>,
    onEditActivityClick: (String) -> Unit,
    onDeleteRequested: (String) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        itemsIndexed(
            items = items,
            key = { _, item -> item.id }
        ) { index, item ->
            val dismissState = rememberSwipeToDismissBoxState(
                confirmValueChange = { dismissValue ->
                    if (dismissValue == SwipeToDismissBoxValue.EndToStart || dismissValue == SwipeToDismissBoxValue.StartToEnd) {
                        onDeleteRequested(item.id)
                    }
                    false
                }
            )

            AnimatedVisibility(
                visible = true,
                enter = fadeIn(animationSpec = tween(durationMillis = 280, delayMillis = index * 30)) +
                    slideInVertically(
                        animationSpec = tween(durationMillis = 320, delayMillis = index * 30),
                        initialOffsetY = { fullHeight -> fullHeight / 5 }
                    )
            ) {
                SwipeToDismissBox(
                    state = dismissState,
                    backgroundContent = {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(vertical = 4.dp)
                                .background(
                                    color = MaterialTheme.colorScheme.error.copy(alpha = 0.15f),
                                    shape = RoundedCornerShape(16.dp)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = stringResource(id = R.string.activities_swipe_delete_hint),
                                style = MaterialTheme.typography.labelLarge,
                                color = MaterialTheme.colorScheme.error,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                ) {
                    ActivityCard(
                        activity = item,
                        modifier = Modifier.clickable { onEditActivityClick(item.id) }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FoodList(
    items: List<FoodUiModel>,
    onEditFoodClick: (String) -> Unit,
    onDeleteRequested: (String) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        itemsIndexed(
            items = items,
            key = { _, item -> item.id }
        ) { index, item ->
            val dismissState = rememberSwipeToDismissBoxState(
                confirmValueChange = { dismissValue ->
                    if (dismissValue == SwipeToDismissBoxValue.EndToStart || dismissValue == SwipeToDismissBoxValue.StartToEnd) {
                        onDeleteRequested(item.id)
                    }
                    false
                }
            )

            AnimatedVisibility(
                visible = true,
                enter = fadeIn(animationSpec = tween(durationMillis = 280, delayMillis = index * 30)) +
                    slideInVertically(
                        animationSpec = tween(durationMillis = 320, delayMillis = index * 30),
                        initialOffsetY = { fullHeight -> fullHeight / 5 }
                    )
            ) {
                SwipeToDismissBox(
                    state = dismissState,
                    backgroundContent = {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(vertical = 4.dp)
                                .background(
                                    color = MaterialTheme.colorScheme.error.copy(alpha = 0.15f),
                                    shape = RoundedCornerShape(16.dp)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = stringResource(id = R.string.food_swipe_delete_hint),
                                style = MaterialTheme.typography.labelLarge,
                                color = MaterialTheme.colorScheme.error,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                ) {
                    FoodCard(
                        food = item,
                        modifier = Modifier.clickable { onEditFoodClick(item.id) }
                    )
                }
            }
        }
    }
}

@Composable
private fun EmptyStateView(
    title: String,
    message: String,
    actionLabel: String,
    onAddClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        contentAlignment = Alignment.Center
    ) {
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.97f)
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 18.dp, vertical = 18.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
                Text(
                    text = message,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(6.dp))
                Button(onClick = onAddClick) {
                    Text(text = actionLabel)
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ActivitiesScreenPreview() {
    SmartFitTheme(dynamicColor = false) {
        ActivitiesScreen(
            uiState = ActivitiesScreenState(
                allActivities = ActivitiesMockData.activities,
                allFoods = ActivitiesMockData.foods,
                isLoading = false
            ),
            onTabChanged = {},
            onActivityFilterChanged = {},
            onActivitySortChanged = {},
            onFoodFilterChanged = {},
            onFoodSortChanged = {},
            onRefreshClick = {},
            onSortMenuExpandedChanged = {},
            onRequestDeleteActivity = {},
            onRequestDeleteFood = {},
            onDismissDeleteDialogs = {},
            onConfirmDeleteActivity = {},
            onConfirmDeleteFood = {},
            onUndoDeleteActivity = {},
            onUndoDeleteFood = {},
            onConsumeDeletedActivityUndo = {},
            onConsumeDeletedFoodUndo = {},
            onAddActivityClick = {},
            onEditActivityClick = {},
            onAddFoodClick = {},
            onEditFoodClick = {}
        )
    }
}


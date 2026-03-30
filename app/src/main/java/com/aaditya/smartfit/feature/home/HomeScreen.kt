package com.aaditya.smartfit.feature.home

import android.net.Uri
import android.graphics.Color as AndroidColor
import android.graphics.Typeface
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DirectionsWalk
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.ShowChart
import androidx.compose.material.icons.filled.Timeline
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import coil.compose.AsyncImage
import com.aaditya.smartfit.R
import com.aaditya.smartfit.feature.activities.list.ActivityType
import com.aaditya.smartfit.ui.theme.SmartFitBlue
import com.aaditya.smartfit.ui.theme.SmartFitGreen
import com.aaditya.smartfit.ui.theme.SmartFitTheme
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.formatter.ValueFormatter
import kotlin.math.roundToInt
import java.util.Calendar

private enum class ChartMode {
    DAILY,
    WEEKLY
}

private data class SummaryCardData(
    val value: String,
    val label: String,
    val icon: ImageVector,
    val accent: Color
)

private data class BarMetric(
    val label: String,
    val value: Float
)

private data class DonutMetric(
    val label: String,
    val value: Float,
    val color: Color
)

private data class GoalStatusData(
    val title: String,
    val value: String,
    val progress: Float,
    val accent: Color
)

private data class RecentActivityData(
    val title: String,
    val meta: String,
    val time: String,
    val icon: ImageVector,
    val accent: Color
)

private fun Int.formatWithCommas(): String = String.format("%,d", this)

@Composable
fun HomeScreen(
    uiState: HomeUiState = HomeUiState(isLoading = false),
    modifier: Modifier = Modifier,
    profileImageUri: Uri? = null,
    onAddActivityClick: () -> Unit = {},
    onViewActivitiesClick: () -> Unit = {},
    onViewTipsClick: () -> Unit = {}
) {
    var chartMode by rememberSaveable { mutableStateOf(ChartMode.DAILY) }

    val dailyBarMetrics = listOf(
        BarMetric(stringResource(id = R.string.home_bar_morning), 130f),
        BarMetric(stringResource(id = R.string.home_bar_afternoon), 170f),
        BarMetric(stringResource(id = R.string.home_bar_evening), 220f),
        BarMetric(stringResource(id = R.string.home_bar_night), 95f)
    )

    val weeklyBarMetrics = listOf(
        BarMetric(stringResource(id = R.string.home_bar_mon), 380f),
        BarMetric(stringResource(id = R.string.home_bar_tue), 460f),
        BarMetric(stringResource(id = R.string.home_bar_wed), 510f),
        BarMetric(stringResource(id = R.string.home_bar_thu), 440f),
        BarMetric(stringResource(id = R.string.home_bar_fri), 490f),
        BarMetric(stringResource(id = R.string.home_bar_sat), 560f),
        BarMetric(stringResource(id = R.string.home_bar_sun), 420f)
    )

    val dailyDonutMetrics = listOf(
        DonutMetric(stringResource(id = R.string.home_legend_walking), 42f, SmartFitGreen),
        DonutMetric(stringResource(id = R.string.home_legend_running), 28f, SmartFitBlue),
        DonutMetric(stringResource(id = R.string.home_legend_gym), 20f, Color(0xFFFFA726)),
        DonutMetric(stringResource(id = R.string.home_legend_yoga), 10f, Color(0xFF8E24AA))
    )

    val weeklyDonutMetrics = listOf(
        DonutMetric(stringResource(id = R.string.home_legend_walking), 36f, SmartFitGreen),
        DonutMetric(stringResource(id = R.string.home_legend_running), 31f, SmartFitBlue),
        DonutMetric(stringResource(id = R.string.home_legend_gym), 24f, Color(0xFFFFA726)),
        DonutMetric(stringResource(id = R.string.home_legend_yoga), 9f, Color(0xFF8E24AA))
    )

    val goalStatusItems = listOf(
        GoalStatusData(
            title = stringResource(id = R.string.home_goal_steps_label),
            value = stringResource(
                id = R.string.home_goal_steps_value_dynamic,
                uiState.stepsToday,
                uiState.stepGoal
            ),
            progress = (uiState.stepsToday.toFloat() / uiState.stepGoal.toFloat())
                .coerceIn(0f, 1f),
            accent = SmartFitGreen
        ),
        GoalStatusData(
            title = stringResource(id = R.string.home_goal_calories_label),
            value = stringResource(
                id = R.string.home_goal_calories_value_dynamic,
                uiState.caloriesBurnedToday,
                uiState.calorieGoal
            ),
            progress = (uiState.caloriesBurnedToday.toFloat() / uiState.calorieGoal.toFloat())
                .coerceIn(0f, 1f),
            accent = SmartFitBlue
        ),
        GoalStatusData(
            title = stringResource(id = R.string.home_goal_minutes_label),
            value = stringResource(
                id = R.string.home_goal_minutes_value_dynamic,
                uiState.activeMinutesToday,
                uiState.workoutGoalMinutes
            ),
            progress = (uiState.activeMinutesToday.toFloat() / uiState.workoutGoalMinutes.toFloat())
                .coerceIn(0f, 1f),
            accent = Color(0xFFFFA726)
        )
    )

    val overallGoalProgress = goalStatusItems
        .map { it.progress.coerceIn(0f, 1f) }
        .average()
        .toFloat()

    val progressRatio by animateFloatAsState(
        targetValue = overallGoalProgress,
        animationSpec = tween(durationMillis = 1000, easing = FastOutSlowInEasing),
        label = "homeProgressRatio"
    )

    val overallGoalProgressPercent = (progressRatio * 100f).roundToInt()

    val summaryItems = listOf(
        SummaryCardData(
            value = uiState.stepsToday.formatWithCommas(),
            label = stringResource(id = R.string.home_summary_steps_label),
            icon = Icons.Filled.DirectionsWalk,
            accent = SmartFitGreen
        ),
        SummaryCardData(
            value = stringResource(
                id = R.string.home_summary_kcal_value,
                uiState.caloriesIntakeToday
            ),
            label = stringResource(id = R.string.home_summary_calories_intake_label),
            icon = Icons.Filled.LocalFireDepartment,
            accent = Color(0xFFFF8A65)
        ),
        SummaryCardData(
            value = uiState.workoutsCount.toString(),
            label = stringResource(id = R.string.home_summary_workouts_label),
            icon = Icons.Filled.FitnessCenter,
            accent = SmartFitBlue
        ),
        SummaryCardData(
            value = stringResource(
                id = R.string.home_summary_overall_progress_value,
                overallGoalProgressPercent
            ),
            label = stringResource(id = R.string.home_summary_overall_goal_label),
            icon = Icons.Filled.ShowChart,
            accent = Color(0xFF7E57C2)
        )
    )

    val dayPartGreeting = when (Calendar.getInstance().get(Calendar.HOUR_OF_DAY)) {
        in 5..11 -> stringResource(id = R.string.home_greeting_morning)
        in 12..16 -> stringResource(id = R.string.home_greeting_afternoon)
        else -> stringResource(id = R.string.home_greeting_evening)
    }

    val greetingTitleText = stringResource(
        id = R.string.home_greeting_personalized,
        uiState.greetingName,
        dayPartGreeting
    )

    val recentActivities = uiState.recentActivities.map { recent ->
        RecentActivityData(
            title = recent.title,
            meta = recent.meta,
            time = recent.time,
            icon = when (recent.type) {
                ActivityType.WALKING -> Icons.Filled.DirectionsWalk
                ActivityType.RUNNING -> Icons.Filled.ShowChart
                ActivityType.CYCLING -> Icons.Filled.Timeline
                ActivityType.STRENGTH -> Icons.Filled.FitnessCenter
                ActivityType.YOGA -> Icons.Filled.WbSunny
            },
            accent = when (recent.type) {
                ActivityType.WALKING -> SmartFitGreen
                ActivityType.RUNNING -> SmartFitBlue
                ActivityType.CYCLING -> Color(0xFF26A69A)
                ActivityType.STRENGTH -> Color(0xFFFFA726)
                ActivityType.YOGA -> Color(0xFF8E24AA)
            }
        )
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
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(top = 16.dp, bottom = 96.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                HomeTopBar(profileImageUri = profileImageUri)
            }

            item {
                GreetingSection(greetingText = greetingTitleText)
            }

            item {
                SummaryCardRow(items = summaryItems)
            }

            item {
                QuickActionsSection(
                    onAddActivityClick = onAddActivityClick,
                    onViewActivitiesClick = onViewActivitiesClick,
                    onViewTipsClick = onViewTipsClick
                )
            }

            item {
                HomeGoalsSummaryCard(
                    stepGoal = uiState.stepGoal,
                    workoutGoalMinutes = uiState.workoutGoalMinutes,
                    calorieGoal = uiState.calorieGoal
                )
            }

            item {
                ChartSection(
                    mode = chartMode,
                    onModeChange = { chartMode = it },
                    dailyBarMetrics = dailyBarMetrics,
                    weeklyBarMetrics = weeklyBarMetrics,
                    dailyDonutMetrics = dailyDonutMetrics,
                    weeklyDonutMetrics = weeklyDonutMetrics
                )
            }

            item {
                GoalStatusSection(items = goalStatusItems)
            }

            item {
                RecentActivitySection(
                    items = recentActivities,
                    onViewActivitiesClick = onViewActivitiesClick
                )
            }

            item {
                InsightsSection()
            }
        }
    }
}

@Composable
private fun HomeTopBar(profileImageUri: Uri?) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = stringResource(id = R.string.home_screen_title),
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )

        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = { }) {
                Icon(
                    imageVector = Icons.Filled.Notifications,
                    contentDescription = stringResource(id = R.string.home_notifications)
                )
            }
            Surface(
                shape = CircleShape,
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.14f)
            ) {
                if (profileImageUri != null) {
                    AsyncImage(
                        model = profileImageUri,
                        contentDescription = stringResource(id = R.string.profile_avatar_content_description),
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Text(
                        text = "S",
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}

@Composable
private fun HomeGoalsSummaryCard(
    stepGoal: Int,
    workoutGoalMinutes: Int,
    calorieGoal: Int
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.97f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(
                text = stringResource(id = R.string.home_goals_summary_title),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = stringResource(id = R.string.home_goals_summary_subtitle),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            SummaryMetricRow(
                icon = Icons.Filled.DirectionsWalk,
                label = stringResource(id = R.string.home_daily_step_goal_label),
                value = stringResource(
                    id = R.string.home_daily_step_goal_value_dynamic,
                    stepGoal.formatWithCommas()
                )
            )
            SummaryMetricRow(
                icon = Icons.Filled.FitnessCenter,
                label = stringResource(id = R.string.home_daily_workout_goal_label),
                value = stringResource(
                    id = R.string.home_daily_workout_goal_value_dynamic,
                    workoutGoalMinutes
                )
            )
            SummaryMetricRow(
                icon = Icons.Filled.LocalFireDepartment,
                label = stringResource(id = R.string.home_daily_calorie_goal_label),
                value = stringResource(
                    id = R.string.home_daily_calorie_goal_value_dynamic,
                    calorieGoal.formatWithCommas()
                )
            )
        }
    }
}

@Composable
private fun SummaryMetricRow(
    icon: ImageVector,
    label: String,
    value: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            modifier = Modifier.weight(1f),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium
            )
        }
        Text(
            text = value,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.primary
        )
    }
}

@Composable
private fun GreetingSection(greetingText: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.96f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 16.dp)
        ) {
            Text(
                text = greetingText,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.ExtraBold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = stringResource(id = R.string.home_subtitle),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun SummaryCardRow(items: List<SummaryCardData>) {
    Column {
        Text(
            text = stringResource(id = R.string.home_daily_summary_title),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold
        )
        Spacer(modifier = Modifier.height(10.dp))
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            items.chunked(2).forEach { rowItems ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    rowItems.forEach { item ->
                        SummaryGridCard(
                            item = item,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    if (rowItems.size == 1) {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
        }
    }
}

@Composable
private fun SummaryGridCard(
    item: SummaryCardData,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.height(148.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.96f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Surface(
                shape = CircleShape,
                color = item.accent.copy(alpha = 0.16f)
            ) {
                Icon(
                    imageVector = item.icon,
                    contentDescription = item.label,
                    tint = item.accent,
                    modifier = Modifier.padding(8.dp)
                )
            }
            Text(
                text = item.value,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.ExtraBold
            )
            Text(
                text = item.label,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun QuickActionsSection(
    onAddActivityClick: () -> Unit,
    onViewActivitiesClick: () -> Unit,
    onViewTipsClick: () -> Unit
) {
    Column {
        Text(
            text = stringResource(id = R.string.home_quick_actions_title),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold
        )
        Spacer(modifier = Modifier.height(10.dp))

        LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            item {
                QuickActionCard(
                    title = stringResource(id = R.string.home_quick_action_add),
                    icon = Icons.Outlined.Add,
                    accent = SmartFitGreen,
                    onClick = onAddActivityClick
                )
            }
            item {
                QuickActionCard(
                    title = stringResource(id = R.string.home_quick_action_activities),
                    icon = Icons.Filled.Timeline,
                    accent = SmartFitBlue,
                    onClick = onViewActivitiesClick
                )
            }
            item {
                QuickActionCard(
                    title = stringResource(id = R.string.home_quick_action_tips),
                    icon = Icons.Filled.WbSunny,
                    accent = Color(0xFFFFA726),
                    onClick = onViewTipsClick
                )
            }
        }
    }
}

@Composable
private fun QuickActionCard(
    title: String,
    icon: ImageVector,
    accent: Color,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .width(162.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.97f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = CircleShape,
                color = accent.copy(alpha = 0.16f)
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = accent,
                    modifier = Modifier.padding(8.dp)
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
private fun ChartSection(
    mode: ChartMode,
    onModeChange: (ChartMode) -> Unit,
    dailyBarMetrics: List<BarMetric>,
    weeklyBarMetrics: List<BarMetric>,
    dailyDonutMetrics: List<DonutMetric>,
    weeklyDonutMetrics: List<DonutMetric>
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.97f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = stringResource(id = R.string.home_chart_title),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = stringResource(id = R.string.home_chart_subtitle),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                ChartModeChip(
                    text = stringResource(id = R.string.home_daily_tab),
                    selected = mode == ChartMode.DAILY,
                    onClick = { onModeChange(ChartMode.DAILY) }
                )
                ChartModeChip(
                    text = stringResource(id = R.string.home_weekly_tab),
                    selected = mode == ChartMode.WEEKLY,
                    onClick = { onModeChange(ChartMode.WEEKLY) }
                )
            }

            if (mode == ChartMode.DAILY) {
                ModernBarChartCard(
                    title = stringResource(id = R.string.home_daily_bar_chart_title),
                    subtitle = stringResource(id = R.string.home_daily_bar_chart_subtitle),
                    data = dailyBarMetrics,
                    accent = SmartFitGreen
                )
                ModernDonutChartCard(
                    title = stringResource(id = R.string.home_daily_donut_chart_title),
                    subtitle = stringResource(id = R.string.home_daily_donut_chart_subtitle),
                    centerText = stringResource(id = R.string.home_chart_center_today),
                    data = dailyDonutMetrics
                )
            } else {
                ModernBarChartCard(
                    title = stringResource(id = R.string.home_weekly_bar_chart_title),
                    subtitle = stringResource(id = R.string.home_weekly_bar_chart_subtitle),
                    data = weeklyBarMetrics,
                    accent = SmartFitBlue
                )
                ModernDonutChartCard(
                    title = stringResource(id = R.string.home_weekly_donut_chart_title),
                    subtitle = stringResource(id = R.string.home_weekly_donut_chart_subtitle),
                    centerText = stringResource(id = R.string.home_chart_center_week),
                    data = weeklyDonutMetrics
                )
            }
        }
    }
}

@Composable
private fun ChartModeChip(
    text: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        shape = RoundedCornerShape(50),
        colors = ButtonDefaults.buttonColors(
            containerColor = if (selected) SmartFitGreen else MaterialTheme.colorScheme.surfaceVariant,
            contentColor = if (selected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
        ),
        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp)
    ) {
        Text(text = text)
    }
}

@Composable
private fun ModernBarChartCard(
    title: String,
    subtitle: String,
    data: List<BarMetric>,
    accent: Color
) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.06f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            SmartFitBarChart(
                data = data,
                barColor = accent
            )

            peakMetric(data)?.let { peak ->
                Text(
                    text = stringResource(
                        id = R.string.home_chart_peak_format,
                        peak.label,
                        peak.value.roundToInt()
                    ),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@Composable
private fun ModernDonutChartCard(
    title: String,
    subtitle: String,
    centerText: String,
    data: List<DonutMetric>
) {
    val total = data.sumOf { it.value.toDouble() }.toFloat().coerceAtLeast(1f)

    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.06f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            SmartFitDonutChart(
                data = data,
                centerText = centerText
            )

            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                data.forEach { item ->
                    DonutLegendRow(
                        label = item.label,
                        value = item.value,
                        percent = ((item.value / total) * 100f).roundToInt(),
                        color = item.color
                    )
                }
            }
        }
    }
}

@Composable
private fun DonutLegendRow(
    label: String,
    value: Float,
    percent: Int,
    color: Color
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(10.dp)
                    .clip(CircleShape)
                    .background(color)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Text(
            text = "${value.roundToInt()}% • $percent%",
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
private fun SmartFitBarChart(
    data: List<BarMetric>,
    barColor: Color
) {
    val axisTextColor = MaterialTheme.colorScheme.onSurfaceVariant.toArgb()
    val barValueTextColor = MaterialTheme.colorScheme.onSurface.toArgb()
    val gridColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f).toArgb()

    AndroidView(
        modifier = Modifier
            .fillMaxWidth()
            .height(220.dp),
        factory = { context ->
            BarChart(context).apply {
                description.isEnabled = false
                legend.isEnabled = false
                setPinchZoom(false)
                setScaleEnabled(false)
                setDoubleTapToZoomEnabled(false)
                setDrawGridBackground(false)
                setDrawBarShadow(false)
                setFitBars(true)
                axisRight.isEnabled = false

                axisLeft.apply {
                    axisMinimum = 0f
                    textColor = axisTextColor
                    this.gridColor = gridColor
                }

                xAxis.apply {
                    position = XAxis.XAxisPosition.BOTTOM
                    granularity = 1f
                    setDrawGridLines(false)
                    textColor = axisTextColor
                    labelRotationAngle = -20f
                }

                setNoDataText("")
            }
        },
        update = { chart ->
            val entries = data.mapIndexed { index, metric ->
                BarEntry(index.toFloat(), metric.value)
            }

            val set = BarDataSet(entries, "").apply {
                color = barColor.toArgb()
                valueTextColor = barValueTextColor
                valueTextSize = 10f
                valueFormatter = object : ValueFormatter() {
                    override fun getBarLabel(barEntry: BarEntry?): String {
                        return barEntry?.y?.roundToInt()?.toString() ?: ""
                    }
                }
            }

            chart.data = BarData(set).apply { barWidth = 0.56f }
            chart.xAxis.valueFormatter = IndexAxisValueFormatter(data.map { it.label })
            chart.axisLeft.axisMaximum = (data.maxOfOrNull { it.value } ?: 0f) * 1.25f
            chart.animateY(800)
            chart.invalidate()
        }
    )
}

@Composable
private fun SmartFitDonutChart(
    data: List<DonutMetric>,
    centerText: String
) {
    val valueTextColor = MaterialTheme.colorScheme.onSurface.toArgb()

    AndroidView(
        modifier = Modifier
            .fillMaxWidth()
            .height(220.dp),
        factory = { context ->
            PieChart(context).apply {
                description.isEnabled = false
                legend.isEnabled = false
                setDrawEntryLabels(false)
                isDrawHoleEnabled = true
                holeRadius = 62f
                transparentCircleRadius = 66f
                setHoleColor(AndroidColor.TRANSPARENT)
                setCenterTextTypeface(Typeface.DEFAULT_BOLD)
                setCenterTextSize(13f)
                setUsePercentValues(false)
                setExtraOffsets(4f, 4f, 4f, 4f)
                setNoDataText("")
            }
        },
        update = { chart ->
            val entries = data.map { metric ->
                PieEntry(metric.value, metric.label)
            }

            val set = PieDataSet(entries, "").apply {
                colors = data.map { it.color.toArgb() }
                sliceSpace = 2f
                setDrawValues(false)
            }

            chart.data = PieData(set)
            chart.centerText = centerText
            chart.setCenterTextColor(valueTextColor)
            chart.animateY(800)
            chart.invalidate()
        }
    )
}

@Composable
private fun GoalStatusSection(items: List<GoalStatusData>) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.97f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = stringResource(id = R.string.home_goal_status_title),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = stringResource(id = R.string.home_goal_status_subtitle),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            items.forEach { item ->
                GoalStatusRow(item = item)
            }
        }
    }
}

@Composable
private fun GoalStatusRow(item: GoalStatusData) {
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = item.title,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = item.value,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.SemiBold,
                color = item.accent
            )
        }
        LinearProgressIndicator(
            progress = item.progress,
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(RoundedCornerShape(50)),
            color = item.accent,
            trackColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.14f)
        )
    }
}

@Composable
private fun RecentActivitySection(
    items: List<RecentActivityData>,
    onViewActivitiesClick: () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = stringResource(id = R.string.home_recent_activity_title),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold
        )
        Text(
            text = stringResource(id = R.string.home_recent_activity_subtitle),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        items.forEach { item ->
            RecentActivityCard(item = item)
        }

        TextButton(
            onClick = onViewActivitiesClick,
            modifier = Modifier.align(Alignment.End)
        ) {
            Text(text = stringResource(id = R.string.home_recent_activity_view_all))
        }
    }
}

@Composable
private fun RecentActivityCard(item: RecentActivityData) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.98f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = CircleShape,
                color = item.accent.copy(alpha = 0.16f)
            ) {
                Icon(
                    imageVector = item.icon,
                    contentDescription = item.title,
                    tint = item.accent,
                    modifier = Modifier.padding(8.dp)
                )
            }
            Spacer(modifier = Modifier.width(10.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.title,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = item.meta,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            Text(
                text = item.time,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun InsightsSection() {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = stringResource(id = R.string.home_insights_title),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold
        )

        InsightCard(
            icon = Icons.Filled.WbSunny,
            text = stringResource(id = R.string.home_insight_one)
        )
        InsightCard(
            icon = Icons.Filled.ShowChart,
            text = stringResource(id = R.string.home_insight_two)
        )
    }
}

@Composable
private fun InsightCard(
    icon: ImageVector,
    text: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.98f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = CircleShape,
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.14f)
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(8.dp)
                )
            }
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = text,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

private fun peakMetric(data: List<BarMetric>): BarMetric? {
    return data.maxByOrNull { it.value }
}

@Preview(showBackground = true)
@Composable
private fun HomeScreenPreview() {
    SmartFitTheme(dynamicColor = false) {
        HomeScreen()
    }
}


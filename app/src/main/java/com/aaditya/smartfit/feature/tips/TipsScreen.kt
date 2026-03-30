package com.aaditya.smartfit.feature.tips

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.BrokenImage
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.SentimentDissatisfied
import androidx.compose.material.icons.outlined.WarningAmber
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.aaditya.smartfit.R
import com.aaditya.smartfit.core.ui.components.TipCard
import com.aaditya.smartfit.core.ui.components.TipCardModel
import com.aaditya.smartfit.ui.theme.SmartFitBlue
import com.aaditya.smartfit.ui.theme.SmartFitGreen
import com.aaditya.smartfit.ui.theme.SmartFitTheme
import coil.compose.SubcomposeAsyncImage
import coil.request.ImageRequest
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TipsScreen(
    modifier: Modifier = Modifier
) {
    val scope = rememberCoroutineScope()

    var allTips by remember { mutableStateOf<List<TipCardModel>>(emptyList()) }
    var isLoading by rememberSaveable { mutableStateOf(true) }
    var hasError by rememberSaveable { mutableStateOf(false) }
    var selectedTip by remember { mutableStateOf<TipCardModel?>(null) }

    var searchQuery by rememberSaveable { mutableStateOf("") }
    var selectedCategory by rememberSaveable { mutableStateOf(TipsCategory.ALL) }

    fun loadTips() {
        scope.launch {
            isLoading = true
            hasError = false

            runCatching {
                TipsRemoteSource.fetchTips(limit = 24)
            }.onSuccess { tips ->
                allTips = tips
                selectedTip = null
            }.onFailure {
                hasError = true
                allTips = emptyList()
                selectedTip = null
            }

            isLoading = false
        }
    }

    LaunchedEffect(Unit) {
        loadTips()
    }

    val filteredTips = remember(searchQuery, selectedCategory, allTips) {
        allTips.filter { tip ->
            val matchesCategory = when (selectedCategory) {
                TipsCategory.ALL -> true
                TipsCategory.WORKOUT -> tip.categoryLabel.equals("Workout", ignoreCase = true)
                TipsCategory.NUTRITION -> tip.categoryLabel.equals("Nutrition", ignoreCase = true)
                TipsCategory.LIFESTYLE -> tip.categoryLabel.equals("Lifestyle", ignoreCase = true)
            }

            val normalizedQuery = searchQuery.trim()
            val matchesSearch = normalizedQuery.isBlank() ||
                tip.title.contains(normalizedQuery, ignoreCase = true) ||
                tip.description.contains(normalizedQuery, ignoreCase = true) ||
                tip.categoryLabel.contains(normalizedQuery, ignoreCase = true)

            matchesCategory && matchesSearch
        }
    }

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = stringResource(id = R.string.tips_title),
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = stringResource(id = R.string.tips_subtitle),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                actions = {
                    IconButton(onClick = {
                        searchQuery = ""
                        selectedCategory = TipsCategory.ALL
                        loadTips()
                    }) {
                        Icon(
                            imageVector = Icons.Outlined.Refresh,
                            contentDescription = stringResource(id = R.string.tips_refresh_content_description)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            SmartFitBlue.copy(alpha = 0.07f),
                            MaterialTheme.colorScheme.background
                        )
                    )
                )
        ) {
            SearchSection(
                query = searchQuery,
                onQueryChange = { searchQuery = it }
            )

            CategorySection(
                selectedCategory = selectedCategory,
                onCategorySelected = { selectedCategory = it }
            )

            when {
                isLoading -> {
                    TipsLoadingState()
                }

                hasError -> {
                    TipsErrorState(onRetry = { loadTips() })
                }

                filteredTips.isEmpty() -> {
                    TipsEmptyState()
                }

                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        itemsIndexed(
                            items = filteredTips,
                            key = { _, tip -> tip.id }
                        ) { index, tip ->
                            AnimatedVisibility(
                                visible = true,
                                enter = fadeIn(animationSpec = tween(durationMillis = 240, delayMillis = index * 35)) +
                                    slideInVertically(
                                        animationSpec = tween(durationMillis = 300, delayMillis = index * 35),
                                        initialOffsetY = { fullHeight -> fullHeight / 6 }
                                    )
                            ) {
                                TipCard(
                                    tip = tip,
                                    readMoreLabel = stringResource(id = R.string.tips_read_more),
                                    imageContentDescription = stringResource(id = R.string.tips_card_image_content_description),
                                    onClick = { selectedTip = tip },
                                    onReadMoreClick = { selectedTip = tip }
                                )
                            }
                        }
                    }
                }
            }

            selectedTip?.let { tip ->
                TipReadMoreDialog(
                    tip = tip,
                    onDismiss = { selectedTip = null }
                )
            }
        }
    }
}

@Composable
private fun TipReadMoreDialog(
    tip: TipCardModel,
    onDismiss: () -> Unit
) {
    val scrollState = rememberScrollState()
    val imageContentDescription = stringResource(id = R.string.tips_card_image_content_description)

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = tip.title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(scrollState),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                SubcomposeAsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(tip.imageUrl)
                        .crossfade(true)
                        .build(),
                    contentDescription = imageContentDescription,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(184.dp)
                        .clip(RoundedCornerShape(14.dp)),
                    loading = {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(MaterialTheme.colorScheme.surfaceVariant),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                strokeWidth = 2.dp,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    },
                    error = {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(MaterialTheme.colorScheme.surfaceVariant),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.BrokenImage,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(30.dp)
                            )
                        }
                    }
                )

                Text(
                    text = "${tip.categoryLabel} • ${tip.readDurationLabel}",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.SemiBold
                )

                Text(
                    text = tip.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text(text = stringResource(id = R.string.tips_dialog_close))
            }
        }
    )
}

@Composable
private fun SearchSection(
    query: String,
    onQueryChange: (String) -> Unit
) {
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 10.dp),
        placeholder = {
            Text(text = stringResource(id = R.string.tips_search_placeholder))
        },
        leadingIcon = {
            Icon(
                imageVector = Icons.Outlined.Search,
                contentDescription = stringResource(id = R.string.tips_search_content_description)
            )
        },
        singleLine = true,
        shape = RoundedCornerShape(14.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f),
            unfocusedContainerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.90f)
        )
    )
}

@Composable
private fun CategorySection(
    selectedCategory: TipsCategory,
    onCategorySelected: (TipsCategory) -> Unit
) {
    LazyRow(
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item {
            TipsCategoryChip(
                text = stringResource(id = R.string.tips_category_all),
                selected = selectedCategory == TipsCategory.ALL,
                onClick = { onCategorySelected(TipsCategory.ALL) }
            )
        }
        item {
            TipsCategoryChip(
                text = stringResource(id = R.string.tips_category_workout),
                selected = selectedCategory == TipsCategory.WORKOUT,
                onClick = { onCategorySelected(TipsCategory.WORKOUT) }
            )
        }
        item {
            TipsCategoryChip(
                text = stringResource(id = R.string.tips_category_nutrition),
                selected = selectedCategory == TipsCategory.NUTRITION,
                onClick = { onCategorySelected(TipsCategory.NUTRITION) }
            )
        }
        item {
            TipsCategoryChip(
                text = stringResource(id = R.string.tips_category_lifestyle),
                selected = selectedCategory == TipsCategory.LIFESTYLE,
                onClick = { onCategorySelected(TipsCategory.LIFESTYLE) }
            )
        }
    }
}

@Composable
private fun TipsCategoryChip(
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
            containerColor = if (selected) SmartFitGreen.copy(alpha = 0.16f) else MaterialTheme.colorScheme.surface,
            labelColor = if (selected) SmartFitGreen else MaterialTheme.colorScheme.onSurfaceVariant
        )
    )
}

@Composable
private fun TipsLoadingState() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            CircularProgressIndicator(color = SmartFitGreen)
            Text(
                text = stringResource(id = R.string.tips_loading_message),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun TipsErrorState(
    onRetry: () -> Unit
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
                containerColor = MaterialTheme.colorScheme.error.copy(alpha = 0.08f)
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 18.dp, vertical = 18.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Outlined.WarningAmber,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.error
                )
                Text(
                    text = stringResource(id = R.string.tips_error_title),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
                Text(
                    text = stringResource(id = R.string.tips_error_message),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )
                TextButton(onClick = onRetry) {
                    Text(text = stringResource(id = R.string.tips_retry_button))
                }
            }
        }
    }
}

@Composable
private fun TipsEmptyState() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        contentAlignment = Alignment.Center
    ) {
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.96f)
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 18.dp, vertical = 20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Outlined.SentimentDissatisfied,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = stringResource(id = R.string.tips_empty_title),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
                Text(
                    text = stringResource(id = R.string.tips_empty_message),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun TipsScreenPreview() {
    SmartFitTheme(dynamicColor = false) {
        TipsScreen()
    }
}


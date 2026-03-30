package com.aaditya.smartfit.feature.tips

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aaditya.smartfit.core.ui.components.TipCardModel
import com.aaditya.smartfit.data.repository.TipsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class TipsScreenState(
    val allTips: List<TipCardModel> = emptyList(),
    val isLoading: Boolean = true,
    val hasError: Boolean = false,
    val selectedTip: TipCardModel? = null,
    val searchQuery: String = "",
    val selectedCategory: TipsCategory = TipsCategory.ALL
)

enum class TipsCategory {
    ALL,
    WORKOUT,
    NUTRITION,
    LIFESTYLE
}

class TipsViewModel(
    private val tipsRepository: TipsRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(TipsScreenState())
    val uiState: StateFlow<TipsScreenState> = _uiState.asStateFlow()

    init {
        refresh()
    }

    fun refresh() {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isLoading = true,
                    hasError = false,
                    selectedTip = null
                )
            }

            runCatching { tipsRepository.fetchTips(limit = 24) }
                .onSuccess { tips ->
                    _uiState.update {
                        it.copy(
                            allTips = tips,
                            isLoading = false,
                            hasError = false,
                            selectedTip = null
                        )
                    }
                }
                .onFailure {
                    _uiState.update {
                        it.copy(
                            allTips = emptyList(),
                            isLoading = false,
                            hasError = true,
                            selectedTip = null
                        )
                    }
                }
        }
    }

    fun onSearchQueryChanged(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
    }

    fun onCategorySelected(category: TipsCategory) {
        _uiState.update { it.copy(selectedCategory = category) }
    }

    fun onTipSelected(tip: TipCardModel?) {
        _uiState.update { it.copy(selectedTip = tip) }
    }

    fun clearFiltersAndReload() {
        _uiState.update {
            it.copy(
                searchQuery = "",
                selectedCategory = TipsCategory.ALL
            )
        }
        refresh()
    }
}


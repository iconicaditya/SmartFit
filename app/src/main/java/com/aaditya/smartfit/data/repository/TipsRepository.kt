package com.aaditya.smartfit.data.repository

import com.aaditya.smartfit.core.ui.components.TipCardModel

interface TipsRepository {
    suspend fun fetchTips(limit: Int = 24): List<TipCardModel>
}


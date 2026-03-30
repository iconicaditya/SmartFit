package com.aaditya.smartfit.data.repository.impl

import com.aaditya.smartfit.core.ui.components.TipCardModel
import com.aaditya.smartfit.data.repository.TipsRepository
import com.aaditya.smartfit.feature.tips.TipsRemoteSource

class RemoteTipsRepository : TipsRepository {
    override suspend fun fetchTips(limit: Int): List<TipCardModel> {
        return TipsRemoteSource.fetchTips(limit = limit)
    }
}


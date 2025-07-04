package com.memory.keeper.data.repository

import androidx.annotation.WorkerThread
import com.memory.keeper.core.Resource
import com.memory.keeper.data.dto.NewsInfo
import kotlinx.coroutines.flow.Flow

interface DetailRepository {
    @WorkerThread
    suspend fun fetchNewsDetail(
        id: String,
    ): Flow<Resource<NewsInfo>>
}
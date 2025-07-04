package com.memory.keeper.data.repository

import androidx.annotation.WorkerThread
import com.memory.keeper.core.Resource
import com.memory.keeper.data.model.News
import kotlinx.coroutines.flow.Flow

interface HomeRepository {

    @WorkerThread
    suspend fun fetchHotNews(
        page: Int,
        limit: Int,
    ): Flow<Resource<List<News>>>

    @WorkerThread
    suspend fun fetchLatestNews(
        page: Int,
        limit: Int,
    ): Flow<Resource<List<News>>>
}
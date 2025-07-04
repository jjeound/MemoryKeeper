package com.memory.keeper.data.repository

import androidx.annotation.WorkerThread
import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.memory.keeper.data.Dispatcher
import com.memory.keeper.data.NewsAppDispatchers
import com.memory.keeper.data.database.db.HotNewsDatabase
import com.memory.keeper.data.database.db.LatestNewsDatabase
import com.memory.keeper.data.database.entity.HotNewsEntity
import com.memory.keeper.data.database.entity.LatestNewsEntity
import com.memory.keeper.data.service.HotNewsRemoteMediator
import com.memory.keeper.data.service.NewsClient
import com.memory.keeper.data.service.LatestNewsRemoteMediator
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class MoreRepositoryImpl @Inject constructor(
    private val newsClient: NewsClient,
    private val hotDb: HotNewsDatabase,
    private val latestDb: LatestNewsDatabase,
    @Dispatcher(NewsAppDispatchers.IO) private val ioDispatcher: CoroutineDispatcher,
): MoreRepository{
    @OptIn(ExperimentalPagingApi::class)
    @WorkerThread
    override suspend fun fetchHotNews(
        category: String?
    ): Flow<PagingData<HotNewsEntity>> {
        return Pager(
            config = PagingConfig(pageSize = 10, enablePlaceholders = false),
            remoteMediator = HotNewsRemoteMediator(
                newsClient, hotDb, category
            ),
            pagingSourceFactory = { hotDb.newsDao().getNewsListPaged(category) }
        ).flow.flowOn(ioDispatcher)
    }

    @OptIn(ExperimentalPagingApi::class)
    @WorkerThread
    override suspend fun fetchLatestNews(
        category: String?
    ): Flow<PagingData<LatestNewsEntity>> {
        return Pager(
            config = PagingConfig(pageSize = 10, enablePlaceholders = false),
            remoteMediator = LatestNewsRemoteMediator(
                newsClient, latestDb, category
            ),
            pagingSourceFactory = { latestDb.newsDao().getNewsListPaged(category) }
        ).flow.flowOn(ioDispatcher)
    }
}
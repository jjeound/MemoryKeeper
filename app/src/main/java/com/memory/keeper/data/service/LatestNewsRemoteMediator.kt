package com.memory.keeper.data.service

import android.util.Log
import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.memory.keeper.data.database.db.LatestNewsDatabase
import com.memory.keeper.data.database.entity.LatestNewsEntity
import com.memory.keeper.data.database.entity.mapper.asLatestEntity
import retrofit2.HttpException
import java.io.IOException


@OptIn(ExperimentalPagingApi::class)
class LatestNewsRemoteMediator(
    private val newsClient: NewsClient,
    private val db: LatestNewsDatabase,
    private val category: String? = null
): RemoteMediator<Int, LatestNewsEntity>() {

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, LatestNewsEntity>
    ): MediatorResult {
        return try {
            val loadKey = when(loadType) {
                LoadType.REFRESH -> 1
                LoadType.PREPEND -> return MediatorResult.Success(
                    endOfPaginationReached = true
                )
                LoadType.APPEND -> {
                    val lastItem = state.lastItemOrNull()
                    if(lastItem == null) {
                        return MediatorResult.Success(endOfPaginationReached = true)
                    } else {
                        lastItem.page + 1
                    }
                }
            }

            val response = if (category == null) {
                newsClient.getLatestNewsList(
                    page = loadKey,
                    limit = state.config.pageSize
                )
            } else{
                newsClient.getCategoryLatestNewsList(
                    category = category,
                    page = loadKey,
                )
            }
            Log.d("LatestNewsRemoteMediator", "Response: $response")

            db.withTransaction {
                if(loadType == LoadType.REFRESH) {
                    db.newsDao().clearAll()
                }
                val newsEntities = response.result!!.clusters.map {
                    it.toDomain(loadKey, response.result.pagination.pages)
                }.map {
                    it.asLatestEntity(category = category)
                }
                db.newsDao().insertNewsList(newsEntities)
            }

            MediatorResult.Success(
                endOfPaginationReached = response.result!!.clusters.isEmpty()
            )
        } catch(e: IOException) {
            MediatorResult.Error(e)
        } catch(e: HttpException) {
            MediatorResult.Error(e)
        }
    }
}
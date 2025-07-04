package com.memory.keeper.data.service

import android.util.Log
import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.memory.keeper.data.database.db.HotNewsDatabase
import com.memory.keeper.data.database.entity.HotNewsEntity
import com.memory.keeper.data.database.entity.mapper.asHotEntity
import retrofit2.HttpException
import java.io.IOException


@OptIn(ExperimentalPagingApi::class)
class HotNewsRemoteMediator(
    private val newsClient: NewsClient,
    private val db: HotNewsDatabase,
    private val category: String? = null
): RemoteMediator<Int, HotNewsEntity>() {

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, HotNewsEntity>
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
                newsClient.getHotNewsList(
                    page = loadKey,
                    limit = state.config.pageSize
                )
            } else{
                newsClient.getCategoryHotNewsList(
                    category = category,
                    page = loadKey,
                )
            }
            Log.d("HotNewsRemoteMediator", "Response: $response")

            db.withTransaction {
                if(loadType == LoadType.REFRESH) {
                    db.newsDao().clearAll()
                }
                val newsEntities = response.result!!.clusters.map {
                    it.toDomain(loadKey, response.result.pagination.pages)
                }.map {
                    it.asHotEntity(category = category)
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
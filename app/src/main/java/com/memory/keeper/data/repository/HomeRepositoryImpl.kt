package com.memory.keeper.data.repository

import androidx.annotation.WorkerThread
import com.memory.keeper.core.Resource
import com.memory.keeper.data.Dispatcher
import com.memory.keeper.data.NewsAppDispatchers
import com.memory.keeper.data.model.News
import com.memory.keeper.data.service.NewsClient
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

class HomeRepositoryImpl @Inject constructor(
    private val newsClient: NewsClient,
    @Dispatcher(NewsAppDispatchers.IO) private val ioDispatcher: CoroutineDispatcher,
): HomeRepository{

    @WorkerThread
    override suspend fun fetchHotNews(
        page: Int,
        limit: Int,
    ): Flow<Resource<List<News>>> = flow {
        emit(Resource.Loading())
        try {
            val response = newsClient.getHotNewsList(page = page, limit = limit)
            if(response.isSuccess){
                emit(Resource.Success(
                    response.result!!.clusters.map {
                        it.toDomain(page, response.result.pagination.pages)
                    }))
            } else {
                emit(Resource.Error(response.code))
            }
        }catch (e: HttpException){
            emit(Resource.Error(e.toString()))
        }catch (e: IOException){
            emit(Resource.Error(e.toString()))
        }
    }.flowOn(ioDispatcher)

    @WorkerThread
    override suspend fun fetchLatestNews(
        page: Int,
        limit: Int,
    ): Flow<Resource<List<News>>> = flow {
        emit(Resource.Loading())
        try {
            val response = newsClient.getHotNewsList(page = page, limit = limit)
            if(response.isSuccess){
                emit(Resource.Success(
                    response.result!!.clusters.map {
                        it.toDomain(page, response.result.pagination.pages)
                    }))
            } else {
                emit(Resource.Error(response.code))
            }
        }catch (e: HttpException){
            emit(Resource.Error(e.toString()))
        }catch (e: IOException){
            emit(Resource.Error(e.toString()))
        }
    }.flowOn(ioDispatcher)
}
package com.memory.keeper.data.service

import com.memory.keeper.data.dto.ApiResponse
import com.memory.keeper.data.dto.NewsResponse
import com.memory.keeper.data.dto.NewsInfo
import javax.inject.Inject

class NewsClient @Inject constructor(
    private val newsService: NewsService
){
    suspend fun getHotNewsList(page: Int, limit: Int): ApiResponse<NewsResponse> =
        newsService.getHotNewsList(
            limit = limit,
            page = page,
        )

    suspend fun getLatestNewsList(page: Int, limit: Int): ApiResponse<NewsResponse> =
        newsService.getLatestNewsList(
            limit = limit,
            page = page,
        )
    suspend fun getCategoryHotNewsList(category: String, page: Int): ApiResponse<NewsResponse> =
        newsService.getCategoryHotNewsList(
            category = category,
            limit = PAGING_SIZE,
            page = page,
        )
    suspend fun getCategoryLatestNewsList(category: String, page: Int): ApiResponse<NewsResponse> =
        newsService.getCategoryLatestNewsList(
            category = category,
            limit = PAGING_SIZE,
            page = page,
        )
    suspend fun getNewsDetail(id: String): ApiResponse<NewsInfo> =
        newsService.getNewsDetail(id = id)

    companion object {
        private const val PAGING_SIZE = 10
    }
}
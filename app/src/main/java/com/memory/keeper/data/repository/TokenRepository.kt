package com.memory.keeper.data.repository

import com.memory.keeper.core.Resource
import com.memory.keeper.data.dto.response.ApiResponse
import com.memory.keeper.data.dto.response.Token


interface TokenRepository {
    suspend fun getAccessToken(): String?

    suspend fun getRefreshToken(): String?

    suspend fun saveAccessToken(accessToken: String)

    suspend fun saveRefreshToken(refreshToken: String)

    suspend fun deleteTokens()

    suspend fun refreshToken(refreshToken: String): Resource<ApiResponse<Token>>

    suspend fun refreshAndSaveToken(): Token?
}
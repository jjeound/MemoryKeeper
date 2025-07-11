package com.memory.keeper.data.service

import com.memory.keeper.data.dto.response.ApiResponse
import com.memory.keeper.data.dto.response.Token
import retrofit2.http.Header
import retrofit2.http.POST

interface TokenService {
    @POST("/api/user/refresh")
    suspend fun refreshToken(
        @Header("Refresh-Token") refreshToken: String
    ): ApiResponse<Token>
}
package com.memory.keeper.data.service

import com.memory.keeper.data.dto.request.AccessTokenRequest
import com.memory.keeper.data.dto.response.ApiResponse
import com.memory.keeper.data.dto.response.LoginResponse
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthService {
    @POST("/api/user/kakao-login")
    suspend fun kakaoLogin(
        @Body accessToken: AccessTokenRequest
    ): ApiResponse<LoginResponse>

    @POST("/api/users/logout")
    suspend fun logout(): ApiResponse<String>
}
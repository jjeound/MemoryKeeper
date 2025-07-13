package com.memory.keeper.data.service

import com.memory.keeper.data.dto.request.AccessTokenRequest
import com.memory.keeper.data.dto.request.EmailRequest
import com.memory.keeper.data.dto.response.ApiResponse
import com.memory.keeper.data.dto.response.LoginResponse
import retrofit2.http.Body
import retrofit2.http.POST

interface LoginService {
    @POST("/api/users/kakao-login")
    suspend fun kakaoLogin(
        @Body accessToken: AccessTokenRequest
    ): ApiResponse<LoginResponse>

    @POST("/api/users/login")
    suspend fun testLogin(
        @Body email: EmailRequest
    ): ApiResponse<LoginResponse>
}
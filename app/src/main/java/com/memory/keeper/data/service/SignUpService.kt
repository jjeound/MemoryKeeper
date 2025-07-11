package com.memory.keeper.data.service

import com.memory.keeper.data.dto.request.AccessTokenRequest
import com.memory.keeper.data.dto.request.RelationshipRequest
import com.memory.keeper.data.dto.request.RoleRequest
import com.memory.keeper.data.dto.response.ApiResponse
import com.memory.keeper.data.dto.response.LoginResponse
import com.memory.keeper.data.dto.response.UserInfo
import com.memory.keeper.data.dto.response.UserSearched
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path

interface SignUpService {
    @POST("/api/user/kakao-login")
    suspend fun kakaoLogin(
        @Body accessToken: AccessTokenRequest
    ): ApiResponse<LoginResponse>

    @POST("/api/users/logout")
    suspend fun logout(): ApiResponse<String>

    @PATCH("/api/users")
    suspend fun setRole(
        @Body roleReq: RoleRequest
    ): ApiResponse<String>

    @GET("/api/relationships/search/{email}")
    suspend fun getUserByEmail(
        @Path("email") email: String
    ): ApiResponse<UserSearched>

    @GET("/api/users/basic-info/{user-id}")
    suspend fun getUserById(
        @Path("user-id") userId: Long
    ): ApiResponse<UserInfo>

    @POST("/api/relationships/request")
    suspend fun setRelationship(
        @Body relationshipRequest: RelationshipRequest
    ): ApiResponse<String>
}
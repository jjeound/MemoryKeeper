package com.memory.keeper.data.service

import com.memory.keeper.data.dto.request.RelationshipRequest
import com.memory.keeper.data.dto.request.RoleRequest
import com.memory.keeper.data.dto.response.ApiResponse
import com.memory.keeper.data.dto.response.UserInfo
import com.memory.keeper.data.dto.response.UserSearched
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Query

interface SignUpService {

    @POST("/api/users/logout")
    suspend fun logout(): ApiResponse<String>

    @PATCH("/api/users")
    suspend fun setRole(
        @Body roleReq: RoleRequest
    ): ApiResponse<String>

    @GET("/api/relationships/search")
    suspend fun getUserByEmail(
        @Query("email") email: String
    ): ApiResponse<UserSearched>

    @GET("/api/users/basic-info")
    suspend fun getMyInfo(): ApiResponse<UserInfo>

    @POST("/api/relationships/request")
    suspend fun requestRelationship(
        @Body relationshipRequest: RelationshipRequest
    ): ApiResponse<String>
}
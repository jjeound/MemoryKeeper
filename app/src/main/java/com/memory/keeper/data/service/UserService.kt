package com.memory.keeper.data.service

import com.memory.keeper.data.dto.request.RespondRequest
import com.memory.keeper.data.dto.request.UserInfoRequest
import com.memory.keeper.data.dto.response.ApiResponse
import com.memory.keeper.data.dto.response.MyPatient
import com.memory.keeper.data.dto.response.Notification
import com.memory.keeper.data.dto.response.UserInfoDetail
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface UserService {

    @GET("/api/relationships/notifications")
    suspend fun getNotifications(): ApiResponse<List<Notification>>

    @POST("/api/relationships/respond")
    suspend fun respondRelationship(
        @Body respondRequest: RespondRequest
    ): ApiResponse<String>

    @PUT("/api/userinfo/{targetUserId}")
    suspend fun updateUserDetailInfo(
        @Path("targetUserId") targetUserId: Long,
        @Body userInfoRequest: UserInfoRequest
    ): ApiResponse<String>

    @GET("/api/userinfo/{userId}")
    suspend fun getUserDetailInfo(
        @Path("userId") userId: Long
    ): ApiResponse<UserInfoDetail>

    @GET("/api/relationships/patients")
    suspend fun getMyPatients(): ApiResponse<List<MyPatient>>
}
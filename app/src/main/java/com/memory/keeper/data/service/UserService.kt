package com.memory.keeper.data.service

import com.memory.keeper.data.dto.request.RespondRequest
import com.memory.keeper.data.dto.request.UpdatePhotoInfoRequest
import com.memory.keeper.data.dto.request.UserInfoRequest
import com.memory.keeper.data.dto.response.ApiResponse
import com.memory.keeper.data.dto.response.DailyResponse
import com.memory.keeper.data.dto.response.MonthlyResponse
import com.memory.keeper.data.dto.response.MyPatient
import com.memory.keeper.data.dto.response.Notification
import com.memory.keeper.data.dto.response.UserInfoDetail
import com.memory.keeper.data.dto.response.UserInfoPhoto
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

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

    @GET("/api/userinfo/me")
    suspend fun getMyDetailInfo(): ApiResponse<UserInfoDetail>

    @GET("/api/relationships/patients")
    suspend fun getMyPatients(): ApiResponse<List<MyPatient>>

    @Multipart
    @POST("/api/user-info-photos")
    suspend fun uploadUserInfoPhoto(
        @Part image: MultipartBody.Part,
        @Part("request") photoInfoRequest: RequestBody,
    ): ApiResponse<String>

    @DELETE("/api/user-info-photos/{photoId}")
    suspend fun deleteUserInfoPhoto(
        @Path("photoId") photoId: Long
    ): ApiResponse<String>

    @PATCH("/api/user-info-photos/{photoId}")
    suspend fun modifyUserInfoPhoto(
        @Path("photoId") photoId: Long,
        @Body updatePhotoInfoRequest: UpdatePhotoInfoRequest
    ): ApiResponse<String>

    @GET("/api/user-info-photos/{userInfoId}")
    suspend fun getUserInfoPhotos(
        @Path("userInfoId") userInfoId: Long
    ): ApiResponse<List<UserInfoPhoto>>

    @POST("/api/record/save")
    suspend fun saveDailyRecord(
        @Part("daily") daily: RequestBody,
        @Part dailyImages: List<MultipartBody.Part>?,
        @Part dailyVideos: List<MultipartBody.Part>?,
    )

    @GET("/api/record/get/monthly")
    suspend fun getMonthlyRecord(
        @Query("date") date: String,
    ): ApiResponse<List<MonthlyResponse>>

    @GET("/api/record/get/daily")
    suspend fun getDailyRecord(
        @Query("date") date: String,
        @Query("userId") userId: Long,
    ): ApiResponse<DailyResponse>

    @POST("/api/conversation/save")
    suspend fun saveConversation(
        @Query("date") date: String,
    ): ApiResponse<String>
}
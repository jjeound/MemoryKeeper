package com.memory.keeper.data.service

import com.memory.keeper.data.dto.request.ImageRequest
import com.memory.keeper.data.dto.request.SpeechRequest
import com.memory.keeper.data.dto.response.AIChatResponse
import com.memory.keeper.data.dto.response.ApiResponse
import com.memory.keeper.data.dto.response.DailyIdResponse
import com.memory.keeper.data.dto.response.DailyImage
import com.memory.keeper.data.dto.response.Topic
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Query

interface ChatService {
    @POST("/api/chat/start")
    suspend fun startChat(
        @Body speechRequest: SpeechRequest
    ): ApiResponse<AIChatResponse>

    @GET("/api/chat/topics")
    suspend fun getTopics(): ApiResponse<List<Topic>>

    @POST("/api/record/generate-ai-images")
    suspend fun generateAIImages(
        @Body imageRequest: ImageRequest
    ): ApiResponse<List<DailyImage>>

    @POST("/api/conversation/save")
    suspend fun saveConversation(
        @Query("date") date: String
    ): ApiResponse<DailyIdResponse>

    @Multipart
    @POST("/generate-video")
    suspend fun generateVideo(
        @Part("prompt") prompt: RequestBody,
        @Part image: MultipartBody.Part
    ): ApiResponse<String>
}
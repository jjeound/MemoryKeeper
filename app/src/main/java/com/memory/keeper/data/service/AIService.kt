package com.memory.keeper.data.service

import com.memory.keeper.data.dto.request.ConversationRequest
import com.memory.keeper.data.dto.request.SpeechRequest
import com.memory.keeper.data.dto.response.AIChatResponse
import com.memory.keeper.data.dto.response.ApiResponse
import com.memory.keeper.data.dto.response.Conversations
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.Body
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface AIService {
    @POST("/api/chat/start")
    suspend fun startChat(
        @Body speechRequest: SpeechRequest
    ): ApiResponse<AIChatResponse>

    @POST("/conversation-by-date")
    suspend fun getConversationByDate(
        @Body conversationRequest: ConversationRequest
    ): ApiResponse<Conversations>

    @Multipart
    @POST("/generate-video")
    suspend fun generateVideo(
        @Part("prompt") prompt: RequestBody,
        @Part image: MultipartBody.Part
    ): ApiResponse<String>
}
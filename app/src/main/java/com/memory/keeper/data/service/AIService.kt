package com.memory.keeper.data.service

import com.memory.keeper.data.dto.request.ConversationRequest
import com.memory.keeper.data.dto.request.SpeechRequest
import com.memory.keeper.data.dto.response.AIChatResponse
import com.memory.keeper.data.dto.response.ApiResponse
import com.memory.keeper.data.dto.response.Conversations
import retrofit2.http.Body
import retrofit2.http.POST

interface AIService {
    @POST("/api/chat/start")
    suspend fun startChat(
        @Body speechRequest: SpeechRequest
    ): ApiResponse<AIChatResponse>

    @POST("/conversation-by-date")
    suspend fun getConversationByDate(
        @Body conversationRequest: ConversationRequest
    ): ApiResponse<Conversations>
}
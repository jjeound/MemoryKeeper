package com.memory.keeper.data.repository

import androidx.annotation.WorkerThread
import com.memory.keeper.core.Resource
import com.memory.keeper.data.dto.response.AIChatResponse
import com.memory.keeper.data.dto.response.Topic
import kotlinx.coroutines.flow.Flow

interface ChatRepository {
    @WorkerThread
    fun startChat(userPrompt: String, topic: String): Flow<Resource<AIChatResponse>>
    @WorkerThread
    fun getTopics(): Flow<Resource<List<Topic>>>
    @WorkerThread
    fun generateAIImages(dailyId: Long, images: List<String>): Flow<Resource<List<String>>>
    @WorkerThread
    fun saveConversation(date: String): Flow<Resource<Long>>
}
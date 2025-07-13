package com.memory.keeper.data.repository

import androidx.annotation.WorkerThread
import com.memory.keeper.core.Resource
import com.memory.keeper.data.dto.response.AIChatResponse
import kotlinx.coroutines.flow.Flow

interface AIRepository {
    @WorkerThread
    fun startChat(userPrompt: String): Flow<Resource<AIChatResponse>>
}
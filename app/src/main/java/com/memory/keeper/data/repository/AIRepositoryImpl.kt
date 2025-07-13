package com.memory.keeper.data.repository

import com.memory.keeper.core.Resource
import com.memory.keeper.data.AppDispatchers
import com.memory.keeper.data.Dispatcher
import com.memory.keeper.data.dto.request.AccessTokenRequest
import com.memory.keeper.data.dto.request.SpeechRequest
import com.memory.keeper.data.dto.response.AIChatResponse
import com.memory.keeper.data.service.AIService
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import okio.IOException
import org.json.JSONObject
import retrofit2.HttpException
import javax.inject.Inject

class AIRepositoryImpl @Inject constructor(
    private val aiService: AIService,
    @Dispatcher(AppDispatchers.IO) private val ioDispatcher: CoroutineDispatcher
): AIRepository {
    override fun startChat(userPrompt: String): Flow<Resource<AIChatResponse>> = flow {
        emit(Resource.Loading())
        try {
            val response = aiService.startChat(SpeechRequest(userPrompt))
            if (response.isSuccess){
                emit(Resource.Success(response.result))
            } else {
                emit(Resource.Error(response.message))
            }
        } catch (e: IOException) {
            emit(Resource.Error(e.toString()))
        } catch (e: HttpException) {
            val errorMessage = try {
                val errorJson = e.response()?.errorBody()?.string()
                val errorObj = JSONObject(errorJson ?: "")
                errorObj.getString("message")
            } catch (_: Exception) {
                "다시 말씀해주시겠어요?"
            }
            emit(Resource.Error(errorMessage))
        }
    }.flowOn(ioDispatcher)
}
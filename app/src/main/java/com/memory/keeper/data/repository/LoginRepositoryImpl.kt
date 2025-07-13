package com.memory.keeper.data.repository

import android.content.Context
import androidx.annotation.WorkerThread
import androidx.datastore.preferences.core.edit
import com.memory.keeper.core.PrefKeys.USER_NAME
import com.memory.keeper.core.Resource
import com.memory.keeper.data.AppDispatchers
import com.memory.keeper.data.Dispatcher
import com.memory.keeper.data.dto.request.AccessTokenRequest
import com.memory.keeper.data.dto.request.EmailRequest
import com.memory.keeper.data.dto.response.LoginResponse
import com.memory.keeper.data.service.LoginService
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import okio.IOException
import org.json.JSONObject
import retrofit2.HttpException
import javax.inject.Inject

class LoginRepositoryImpl @Inject constructor(
    private val loginService: LoginService,
    private val tokenRepository: TokenRepository,
    @ApplicationContext context: Context,
    @Dispatcher(AppDispatchers.IO) private val ioDispatcher: CoroutineDispatcher
): LoginRepository {
    val dataStore = context.dataStore

    @WorkerThread
    override fun login(accessToken: String): Flow<Resource<LoginResponse>> = flow {
        emit(Resource.Loading())
        try {
            val response = loginService.kakaoLogin(AccessTokenRequest(accessToken))
            if (response.isSuccess){
                tokenRepository.saveAccessToken(response.result!!.accessToken)
                tokenRepository.saveRefreshToken(response.result.refreshToken)
                saveUserName(response.result.name)
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
                "알 수 없는 오류가 발생했어요."
            }
            emit(Resource.Error(errorMessage))
        }
    }.flowOn(ioDispatcher)

    override fun testLogin(email: String): Flow<Resource<LoginResponse>> = flow {
        emit(Resource.Loading())
        try {
            val response = loginService.testLogin(EmailRequest(email))
            tokenRepository.saveAccessToken(response.result!!.accessToken)
            tokenRepository.saveRefreshToken(response.result.refreshToken)
            saveUserName(response.result.name)
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
                "알 수 없는 오류가 발생했어요."
            }
            emit(Resource.Error(errorMessage))
        }
    }.flowOn(ioDispatcher)

    private suspend fun saveUserName(name: String) {
        dataStore.edit { prefs ->
            prefs[USER_NAME] = name
        }
    }
}
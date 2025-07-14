package com.memory.keeper.data.repository

import android.content.Context
import android.util.Log
import androidx.annotation.WorkerThread
import androidx.datastore.preferences.core.edit
import com.memory.keeper.core.PrefKeys.HAS_SIGNED_UP
import com.memory.keeper.core.PrefKeys.ROLE
import com.memory.keeper.core.PrefKeys.USER_NAME
import com.memory.keeper.core.Resource
import com.memory.keeper.data.AppDispatchers
import com.memory.keeper.data.Dispatcher
import com.memory.keeper.data.dto.request.RelationshipRequest
import com.memory.keeper.data.dto.request.RoleRequest
import com.memory.keeper.data.dto.response.UserInfo
import com.memory.keeper.data.dto.response.UserSearched
import com.memory.keeper.data.service.SignUpService
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import okio.IOException
import org.json.JSONObject
import retrofit2.HttpException
import javax.inject.Inject

class SignUpRepositoryImpl @Inject constructor(
    private val signUpService: SignUpService,
    private val tokenRepository: TokenRepository,
    @ApplicationContext context: Context,
    @Dispatcher(AppDispatchers.IO) private val ioDispatcher: CoroutineDispatcher
): SignUpRepository {
    val dataStore = context.dataStore

    @WorkerThread
    override fun logout(): Flow<Resource<String>> = flow {
        emit(Resource.Loading())
        try {
            val response = signUpService.logout()
            if (response.isSuccess){
                tokenRepository.deleteTokens()
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

    @WorkerThread
    override fun setRole(role: String): Flow<Resource<String>> = flow {
        emit(Resource.Loading())
        try {
            val response = signUpService.setRole(RoleRequest(role = role))
            if (response.isSuccess){
                saveRole(role)
                setHasSignedUp()
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

    @WorkerThread
    override fun getUserByEmail(email: String): Flow<Resource<UserSearched>> = flow {
        emit(Resource.Loading())
        try {
            val response = signUpService.getUserByEmail(email)
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

    @WorkerThread
    override fun getMyInfo(): Flow<Resource<UserInfo>> = flow {
        emit(Resource.Loading())
        try {
            val response = signUpService.getMyInfo()
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

    @WorkerThread
    override fun requestRelationship(userId: Long, type: String): Flow<Resource<String>> = flow {
        emit(Resource.Loading())
        try {
            val response = signUpService.requestRelationship(RelationshipRequest(userId, type))
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

    override suspend fun getUserName(): String? {
        return dataStore.data
            .map { prefs ->
                prefs[USER_NAME]
            }.first()
    }

    private suspend fun saveRole(role: String){
        dataStore.edit { prefs ->
            prefs[ROLE] = role
        }
    }

    private suspend fun setHasSignedUp() {
        dataStore.edit { prefs ->
            prefs[HAS_SIGNED_UP] = true
        }
    }

    override suspend fun getHasSignedUp(): Boolean? {
        return dataStore.data.map { prefs ->
            prefs[HAS_SIGNED_UP]
        }.first()
    }
}
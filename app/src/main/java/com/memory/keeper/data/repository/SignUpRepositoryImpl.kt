package com.memory.keeper.data.repository

import androidx.annotation.WorkerThread
import com.memory.keeper.core.Resource
import com.memory.keeper.data.AppDispatchers
import com.memory.keeper.data.Dispatcher
import com.memory.keeper.data.dto.request.AccessTokenRequest
import com.memory.keeper.data.dto.request.RelationshipRequest
import com.memory.keeper.data.dto.request.RoleRequest
import com.memory.keeper.data.dto.response.LoginResponse
import com.memory.keeper.data.dto.response.UserInfo
import com.memory.keeper.data.dto.response.UserSearched
import com.memory.keeper.data.service.SignUpService
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import okio.IOException
import org.json.JSONObject
import javax.inject.Inject

class SignUpRepositoryImpl @Inject constructor(
    private val signUpService: SignUpService,
    private val tokenRepository: TokenRepository,
    @Dispatcher(AppDispatchers.IO) private val ioDispatcher: CoroutineDispatcher
): SignUpRepository {

    @WorkerThread
    override fun login(accessToken: String): Flow<Resource<LoginResponse>> = flow {
        emit(Resource.Loading())
        try {
            val response = signUpService.kakaoLogin(AccessTokenRequest(accessToken))
            if (response.isSuccess){
                emit(Resource.Success(response.result))
            } else {
                emit(Resource.Error(response.message))
            }
        } catch (e: IOException) {
            emit(Resource.Error(e.toString()))
        } catch (e: retrofit2.HttpException) {
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
        } catch (e: retrofit2.HttpException) {
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
                emit(Resource.Success(response.result))
            } else {
                emit(Resource.Error(response.message))
            }
        } catch (e: IOException) {
            emit(Resource.Error(e.toString()))
        } catch (e: retrofit2.HttpException) {
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
        } catch (e: retrofit2.HttpException) {
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
    override fun getUserById(userId: Long): Flow<Resource<UserInfo>> = flow {
        emit(Resource.Loading())
        try {
            val response = signUpService.getUserById(userId)
            if (response.isSuccess){
                emit(Resource.Success(response.result))
            } else {
                emit(Resource.Error(response.message))
            }
        } catch (e: IOException) {
            emit(Resource.Error(e.toString()))
        } catch (e: retrofit2.HttpException) {
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
    override fun setRelationship(userId: Long, type: String): Flow<Resource<String>> = flow {
        emit(Resource.Loading())
        try {
            val response = signUpService.setRelationship(RelationshipRequest(userId, type))
            if (response.isSuccess){
                emit(Resource.Success(response.result))
            } else {
                emit(Resource.Error(response.message))
            }
        } catch (e: IOException) {
            emit(Resource.Error(e.toString()))
        } catch (e: retrofit2.HttpException) {
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

}
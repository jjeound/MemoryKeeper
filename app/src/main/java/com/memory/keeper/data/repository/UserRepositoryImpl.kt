package com.memory.keeper.data.repository

import android.content.Context
import androidx.annotation.WorkerThread
import com.memory.keeper.core.PrefKeys.ROLE
import com.memory.keeper.core.PrefKeys.USER_NAME
import com.memory.keeper.core.Resource
import com.memory.keeper.data.AppDispatchers
import com.memory.keeper.data.Dispatcher
import com.memory.keeper.data.dto.request.RespondRequest
import com.memory.keeper.data.dto.request.UserInfoRequest
import com.memory.keeper.data.dto.response.MyPatient
import com.memory.keeper.data.dto.response.Notification
import com.memory.keeper.data.dto.response.UserInfoDetail
import com.memory.keeper.data.service.UserService
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

class UserRepositoryImpl @Inject constructor(
    private val userService: UserService,
    @ApplicationContext context: Context,
    @Dispatcher(AppDispatchers.IO) private val ioDispatcher: CoroutineDispatcher
): UserRepository {
    val dataStore = context.dataStore

    @WorkerThread
    override fun getNotifications(): Flow<Resource<List<Notification>>> = flow {
        emit(Resource.Loading())
        try {
            val response = userService.getNotifications()
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
    override fun respondRelationship(
        requestId: Long,
        status: String
    ): Flow<Resource<String>> = flow {
        emit(Resource.Loading())
        try {
            val response = userService.respondRelationship(RespondRequest(requestId, status))
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
    override fun updateUserInfo(
        id: Long,
        updateUserDetailInfo: UserInfoRequest
    ): Flow<Resource<String>> = flow {
        emit(Resource.Loading())
        try {
            val response = userService.updateUserDetailInfo(id, updateUserDetailInfo)
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
    override fun getUserDetailInfo(id: Long): Flow<Resource<UserInfoDetail>> = flow {
        emit(Resource.Loading())
        try {
            val response = userService.getUserDetailInfo(id)
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
    override fun getMyPatients(): Flow<Resource<List<MyPatient>>>  = flow {
        emit(Resource.Loading())
        try {
            val response = userService.getMyPatients()
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

    override suspend fun getMyRole(): String? {
        return dataStore.data.map { prefs ->
            prefs[ROLE]
        }.first()
    }

    override suspend fun getUserName(): String? {
        return dataStore.data.map { prefs ->
            prefs[USER_NAME]
        }.first()
    }
}
package com.memory.keeper.data.repository

import androidx.annotation.WorkerThread
import com.memory.keeper.core.Resource
import com.memory.keeper.data.dto.request.UserInfoRequest
import com.memory.keeper.data.dto.response.MyPatient
import com.memory.keeper.data.dto.response.Notification
import com.memory.keeper.data.dto.response.UserInfoDetail
import kotlinx.coroutines.flow.Flow

interface UserRepository {

    @WorkerThread
    fun getNotifications(): Flow<Resource<List<Notification>>>

    @WorkerThread
    fun respondRelationship(requestId: Long, status: String): Flow<Resource<String>>

    @WorkerThread
    fun updateUserInfo(id: Long, updateUserDetailInfo: UserInfoRequest): Flow<Resource<String>>

    @WorkerThread
    fun getUserDetailInfo(id: Long): Flow<Resource<UserInfoDetail>>

    @WorkerThread
    fun getMyPatients(): Flow<Resource<List<MyPatient>>>

    suspend fun getMyRole(): String?
    suspend fun getUserName(): String?
}
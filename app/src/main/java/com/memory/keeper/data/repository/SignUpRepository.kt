package com.memory.keeper.data.repository

import androidx.annotation.WorkerThread
import com.memory.keeper.core.Resource
import com.memory.keeper.data.dto.response.LoginResponse
import com.memory.keeper.data.dto.response.UserInfo
import com.memory.keeper.data.dto.response.UserSearched
import kotlinx.coroutines.flow.Flow

interface SignUpRepository {
    @WorkerThread
    fun login(accessToken: String): Flow<Resource<LoginResponse>>
    @WorkerThread
    fun logout(): Flow<Resource<String>>
    @WorkerThread
    fun setRole(role: String): Flow<Resource<String>>
    @WorkerThread
    fun getUserByEmail(email: String): Flow<Resource<UserSearched>>
    @WorkerThread
    fun getUserById(userId: Long): Flow<Resource<UserInfo>>
    @WorkerThread
    fun setRelationship(userId: Long, type: String): Flow<Resource<String>>
}
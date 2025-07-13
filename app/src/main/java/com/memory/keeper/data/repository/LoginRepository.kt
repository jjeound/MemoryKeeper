package com.memory.keeper.data.repository

import androidx.annotation.WorkerThread
import com.memory.keeper.core.Resource
import com.memory.keeper.data.dto.response.LoginResponse
import kotlinx.coroutines.flow.Flow

interface LoginRepository {
    @WorkerThread
    fun login(accessToken: String): Flow<Resource<LoginResponse>>
    @WorkerThread
    fun testLogin(email: String): Flow<Resource<LoginResponse>>
}
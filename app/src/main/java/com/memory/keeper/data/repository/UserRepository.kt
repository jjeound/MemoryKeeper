package com.memory.keeper.data.repository

import androidx.annotation.WorkerThread
import com.memory.keeper.core.Resource
import com.memory.keeper.data.dto.request.UserInfoRequest
import com.memory.keeper.data.dto.response.DailyResponse
import com.memory.keeper.data.dto.response.MonthlyResponse
import com.memory.keeper.data.dto.response.MyPatient
import com.memory.keeper.data.dto.response.Notification
import com.memory.keeper.data.dto.response.UserInfoDetail
import com.memory.keeper.data.dto.response.UserInfoPhoto
import kotlinx.coroutines.flow.Flow
import java.io.File

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

    @WorkerThread
    fun getMyDetailInfo(): Flow<Resource<UserInfoDetail>>

    @WorkerThread
    fun uploadUserInfoPhoto(id: Long, description: String, relationToPatient: String, image: File): Flow<Resource<String>>

    @WorkerThread
    fun deleteUserInfoPhoto(photoId: Long): Flow<Resource<String>>

    @WorkerThread
    fun modifyUserInfoPhoto(photoId: Long, description: String, relationToPatient: String): Flow<Resource<String>>

    @WorkerThread
    fun getUserInfoPhotos(userInfoId: Long): Flow<Resource<List<UserInfoPhoto>>>

//    @WorkerThread
//    fun saveDailyRecord(
//        conversation: String, feedback: String?, dailyDayRecording: String,
//    )
    @WorkerThread
    fun getMonthlyRecord(date: String): Flow<Resource<List<MonthlyResponse>>>

    @WorkerThread
    fun getDailyRecord(date: String, userId: Long): Flow<Resource<DailyResponse>>

    @WorkerThread
    fun saveConversation(date: String): Flow<Resource<String>>

    suspend fun getMyRole(): String?
    suspend fun getUserName(): String?
    suspend fun getUserId(): Long?
}
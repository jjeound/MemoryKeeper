package com.memory.keeper.feature.prompt

import androidx.compose.runtime.Stable
import androidx.compose.runtime.mutableIntStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.memory.keeper.core.Resource
import com.memory.keeper.data.dto.request.UserInfoRequest
import com.memory.keeper.data.dto.response.UserInfoDetail
import com.memory.keeper.data.dto.response.UserInfoPhoto
import com.memory.keeper.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

@HiltViewModel
class PromptViewModel @Inject constructor(
    private val userRepository: UserRepository
): ViewModel() {
    val uiState: MutableStateFlow<PromptUIState> =
        MutableStateFlow(PromptUIState.Loading)

    private val _eventFlow: MutableSharedFlow<PromptUIEvent> = MutableSharedFlow()
    val eventFlow = _eventFlow.asSharedFlow()

    private val _patientIds: MutableStateFlow<List<Long>> = MutableStateFlow(emptyList())
    val patientIds: StateFlow<List<Long>> = _patientIds.asStateFlow()

    private val _patientNames: MutableStateFlow<List<String>> = MutableStateFlow(emptyList())
    val patientNames: StateFlow<List<String>> = _patientNames.asStateFlow()

    private val _patientInfos: MutableStateFlow<List<UserInfoDetail>> =
        MutableStateFlow(emptyList())
    val patientInfos: StateFlow<List<UserInfoDetail>> = _patientInfos.asStateFlow()

    private val _myInfo: MutableStateFlow<UserInfoDetail?> =
        MutableStateFlow(null)
    val myInfo: StateFlow<UserInfoDetail?> = _myInfo.asStateFlow()

    private val _userInfoPhoto: MutableStateFlow<List<UserInfoPhoto>> = MutableStateFlow(emptyList())
    val userInfoPhoto: StateFlow<List<UserInfoPhoto>> = _userInfoPhoto.asStateFlow()

    private val _role = MutableStateFlow<String?>(null)
    val role: StateFlow<String?> = _role.asStateFlow()

    private val _userName = MutableStateFlow<String?>(null)
    val userName: StateFlow<String?> = _userName.asStateFlow()

    private val _userId = MutableStateFlow<Long?>(null)
    val userId: StateFlow<Long?> = _userId.asStateFlow()

    var selectedIndex = mutableIntStateOf(0)
        private set

    init {
        getUserId()
        getMyRole()
        getUserName()
    }

    fun getMyPatients() {
        viewModelScope.launch {
            userRepository.getMyPatients().collectLatest {
                when (it) {
                    is Resource.Success -> {
                        if(it.data?.isNotEmpty() == true){
                            _patientIds.value = it.data.map { patient -> patient.userId }
                            _patientNames.value = it.data.map { patient -> patient.name }
                            getUserDetailInfo(it.data.first().userId)
                        } else {
                            uiState.value = PromptUIState.Idle
                        }
                    }
                    is Resource.Error -> {
                        uiState.value = PromptUIState.Error(it.message)
                        _eventFlow.emit(PromptUIEvent.ShowToast(it.message ?: "알 수 없는 오류가 발생했어요."))
                    }
                    is Resource.Loading -> {
                        uiState.value = PromptUIState.Loading
                    }
                }
            }
        }
    }

    fun getUserDetailInfo(id: Long){
        viewModelScope.launch {
            userRepository.getUserDetailInfo(id).collectLatest {
                when (it) {
                    is Resource.Success -> {
                        _patientInfos.value += it.data!!
                        uiState.value = PromptUIState.Idle
                        getUserInfoPhotos(id)
                    }
                    is Resource.Error -> {
                        _eventFlow.emit(PromptUIEvent.ShowToast(it.message ?: "알 수 없는 오류가 발생했어요."))
                        uiState.value = PromptUIState.Error(it.message)
                    }
                    is Resource.Loading -> {
                        uiState.value = PromptUIState.Loading
                    }
                }
            }
        }
    }

    fun getMyDetailInfo(){
        viewModelScope.launch {
            userRepository.getMyDetailInfo().collectLatest {
                when (it) {
                    is Resource.Success -> {
                        _myInfo.value = it.data
                        getUserInfoPhotos(_userId.value!!)
                    }
                    is Resource.Error -> {
                        if (it.message == "USERINFO4004"){
                            _userId.value?.let { id ->
                                _myInfo.value = UserInfoDetail(userInfoId = id)
                            }
                            uiState.value = PromptUIState.Idle
                        } else {
                            _eventFlow.emit(PromptUIEvent.ShowToast(it.message ?: "알 수 없는 오류가 발생했어요."))
                            uiState.value = PromptUIState.Error(it.message)
                        }
                    }
                    is Resource.Loading -> {
                        uiState.value = PromptUIState.Loading
                    }
                }
            }
        }
    }

    fun updateUserInfo(id: Long, userInfoId: Long, userInfoRequest: UserInfoRequest) {
        viewModelScope.launch {
            userRepository.updateUserInfo(id, userInfoRequest).collectLatest {
                when (it) {
                    is Resource.Success -> {
                        _patientInfos.value = _patientInfos.value.map { info ->
                            if (info.userInfoId == userInfoId) {
                                info.copy(
                                    age = userInfoRequest.age,
                                    cognitiveStatus = userInfoRequest.cognitiveStatus,
                                    education = userInfoRequest.education,
                                    familyInfo = userInfoRequest.familyInfo,
                                    forbiddenKeywords = userInfoRequest.forbiddenKeywords,
                                    gender = userInfoRequest.gender,
                                    hometown = userInfoRequest.hometown,
                                    occupation = userInfoRequest.occupation,
                                    lifeHistory = userInfoRequest.lifeHistory,
                                    lifetimeline = userInfoRequest.lifetimeline,
                                )
                            } else {
                                info
                            }
                        }
                        _myInfo.value = UserInfoDetail(
                            userInfoId = 2,
                            age = userInfoRequest.age,
                            cognitiveStatus = userInfoRequest.cognitiveStatus,
                            education = userInfoRequest.education,
                            familyInfo = userInfoRequest.familyInfo,
                            forbiddenKeywords = userInfoRequest.forbiddenKeywords,
                            gender = userInfoRequest.gender,
                            hometown = userInfoRequest.hometown,
                            occupation = userInfoRequest.occupation,
                            lifeHistory = userInfoRequest.lifeHistory,
                            lifetimeline = userInfoRequest.lifetimeline,
                        )
                        _eventFlow.emit(PromptUIEvent.ShowToast("성공적으로 저장 되었습니다."))
                        uiState.value = PromptUIState.Idle
                    }
                    is Resource.Error -> {
                        _eventFlow.emit(PromptUIEvent.ShowToast(it.message ?: "알 수 없는 오류가 발생했어요."))
                        uiState.value = PromptUIState.Error(it.message)
                    }
                    is Resource.Loading -> {
                        uiState.value = PromptUIState.Loading
                    }
                }
            }
        }
    }

    fun uploadUserInfo(id: Long, image: File, description: String, relationToPatient: String){
        viewModelScope.launch {
            userRepository.uploadUserInfoPhoto(id = id, description = description, relationToPatient = relationToPatient, image = image)
                .collectLatest {
                    when(it){
                        is Resource.Success -> {
                            getUserInfoPhotos(id)
                            _eventFlow.emit(PromptUIEvent.ShowToast("사진이 성공적으로 업로드 되었어요."))
                        }
                        is Resource.Error -> {
                            _eventFlow.emit(PromptUIEvent.ShowToast(it.message ?: "알 수 없는 오류가 발생했어요."))
                            uiState.value = PromptUIState.Error(it.message)
                        }
                        is Resource.Loading -> {
                            uiState.value = PromptUIState.Loading
                        }
                    }
                }
        }
    }

    fun deleteUserInfoPhoto(photoId: Long) {
        viewModelScope.launch {
            userRepository.deleteUserInfoPhoto(photoId).collectLatest {
                when (it) {
                    is Resource.Success -> {
                        _userInfoPhoto.value = _userInfoPhoto.value.filter { info ->
                            info.id != photoId
                        }
                        _eventFlow.emit(PromptUIEvent.ShowToast(it.data ?: "사진이 성공적으로 삭제 되었어요."))
                        uiState.value = PromptUIState.Idle
                    }
                    is Resource.Error -> {
                        _eventFlow.emit(PromptUIEvent.ShowToast(it.message ?: "알 수 없는 오류가 발생했어요."))
                        uiState.value = PromptUIState.Error(it.message)
                    }
                    is Resource.Loading -> {
                        uiState.value = PromptUIState.Loading
                    }
                }
            }
        }
    }

    fun modifyUserInfoPhoto(photoId: Long, description: String, relationToPatient: String) {
        viewModelScope.launch {
            userRepository.modifyUserInfoPhoto(photoId, description, relationToPatient).collectLatest {
                when (it) {
                    is Resource.Success -> {
                        _eventFlow.emit(PromptUIEvent.ShowToast("사진이 성공적으로 수정 되었어요."))
                        uiState.value = PromptUIState.Idle
                    }
                    is Resource.Error -> {
                        _eventFlow.emit(PromptUIEvent.ShowToast(it.message ?: "알 수 없는 오류가 발생했어요."))
                        uiState.value = PromptUIState.Error(it.message)
                    }
                    is Resource.Loading -> {
                        uiState.value = PromptUIState.Loading
                    }
                }
            }
        }
    }

    fun getUserInfoPhotos(id: Long){
        viewModelScope.launch {
            userRepository.getUserInfoPhotos(id).collectLatest {
                when (it) {
                    is Resource.Success -> {
                        _userInfoPhoto.value = it.data ?: emptyList()
                        uiState.value = PromptUIState.Idle
                    }
                    is Resource.Error -> {
                        _eventFlow.emit(PromptUIEvent.ShowToast(it.message ?: "알 수 없는 오류가 발생했어요."))
                        uiState.value = PromptUIState.Error(it.message)
                    }
                    is Resource.Loading -> {
                        //uiState.value = PromptUIState.Loading
                    }
                }
            }
        }
    }

    fun getMyRole(){
        viewModelScope.launch {
            _role.update { userRepository.getMyRole()}
        }
    }

    fun getUserName(){
        viewModelScope.launch {
            _userName.update {userRepository.getUserName()}
        }
    }

    fun getUserId(){
        viewModelScope.launch {
            _userId.update { userRepository.getUserId() }
        }
    }

    fun setSelectedIndex(index: Int) {
        selectedIndex.intValue = index
    }
}

@Stable
sealed interface PromptUIState {
    data object Idle : PromptUIState
    data object Loading : PromptUIState
    data class Error(val message: String?) : PromptUIState
}

sealed interface PromptUIEvent {
    data class ShowToast(val message: String): PromptUIEvent
}
package com.memory.keeper.feature.prompt

import android.util.Log
import androidx.compose.runtime.Stable
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.memory.keeper.core.Resource
import com.memory.keeper.data.dto.request.UserInfoRequest
import com.memory.keeper.data.dto.response.UserInfoDetail
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

    private val _role = MutableStateFlow<String?>(null)
    val role: StateFlow<String?> = _role.asStateFlow()

    private val _userName = MutableStateFlow<String?>(null)
    val userName: StateFlow<String?> = _userName.asStateFlow()

    init {
        getMyPatients()
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

    fun updateUserInfo(id: Long, userInfoRequest: UserInfoRequest) {
        viewModelScope.launch {
            userRepository.updateUserInfo(id, userInfoRequest).collectLatest {
                when (it) {
                    is Resource.Success -> {
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
package com.memory.keeper.feature.login

import androidx.compose.runtime.Stable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.memory.keeper.core.Resource
import com.memory.keeper.data.dto.response.UserSearched
import com.memory.keeper.data.repository.LoginRepository
import com.memory.keeper.data.repository.SignUpRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SignUpViewModel @Inject constructor(
    private val loginRepository: LoginRepository,
    private val signUpRepository: SignUpRepository
): ViewModel() {
    val uiState: MutableStateFlow<SignUpUIState> =
        MutableStateFlow(SignUpUIState.Idle)

    private val _eventFlow: MutableSharedFlow<SignUpUIEvent> = MutableSharedFlow()
    val eventFlow = _eventFlow.asSharedFlow()

    private val _name = MutableStateFlow<String?>(null)
    val name = _name.asStateFlow()

    private val _userSearched = MutableStateFlow<UserSearched?>(null)
    val userSearched = _userSearched.asStateFlow()

    fun login(accessToken: String){
        viewModelScope.launch {
            loginRepository.login(accessToken).collectLatest {
                when(it){
                    is Resource.Success -> {
                        _name.value = it.data?.name
                        _eventFlow.emit(SignUpUIEvent.NavigateToNext)
                        uiState.value = SignUpUIState.Idle
                    }
                    is Resource.Error -> {
                        _eventFlow.emit(SignUpUIEvent.ShowToast(it.message ?: "알 수 없는 오류가 발생했어요."))
                        uiState.value = SignUpUIState.Error(it.message)
                    }
                    is Resource.Loading -> {
                        uiState.value = SignUpUIState.Loading
                    }
                }
            }
        }
    }

    fun testLogin(email: String){
        viewModelScope.launch {
            loginRepository.testLogin(email).collectLatest {
                when(it) {
                    is Resource.Success -> {
                        _name.value = "테스트 유저"
                        _eventFlow.emit(SignUpUIEvent.NavigateToNext)
                        uiState.value = SignUpUIState.Idle
                    }
                    is Resource.Error -> {
                        _eventFlow.emit(SignUpUIEvent.ShowToast(it.message ?: "알 수 없는 오류가 발생했어요."))
                        uiState.value = SignUpUIState.Error(it.message)
                    }
                    is Resource.Loading -> {
                        uiState.value = SignUpUIState.Loading
                    }
                }
            }
        }
    }

    fun setRole(role: String) {
        viewModelScope.launch {
            signUpRepository.setRole(role).collectLatest {
                when(it) {
                    is Resource.Success -> {
                        _eventFlow.emit(SignUpUIEvent.NavigateToNext)
                        uiState.value = SignUpUIState.Idle
                    }
                    is Resource.Error -> {
                        _eventFlow.emit(SignUpUIEvent.ShowToast(it.message ?: "알 수 없는 오류가 발생했어요."))
                        uiState.value = SignUpUIState.Error(it.message)
                    }
                    is Resource.Loading -> {
                        uiState.value = SignUpUIState.Loading
                    }
                }
            }
        }
    }

    fun searchUserByEmail(email: String) {
        viewModelScope.launch {
            signUpRepository.getUserByEmail(email).collectLatest {
                when(it) {
                    is Resource.Success -> {
                        _userSearched.value = it.data
                        uiState.value = SignUpUIState.Idle
                    }
                    is Resource.Error -> {
                        _eventFlow.emit(SignUpUIEvent.ShowToast(it.message ?: "알 수 없는 오류가 발생했어요."))
                        uiState.value = SignUpUIState.Error(it.message)
                    }
                    is Resource.Loading -> {
                        uiState.value = SignUpUIState.Loading
                    }
                }
            }
        }
    }

    fun requestRelationship(userId: Long, type: String) {
        viewModelScope.launch {
            signUpRepository.requestRelationship(userId, type).collectLatest {
                when(it) {
                    is Resource.Success -> {
                        _eventFlow.emit(SignUpUIEvent.NavigateToNext)
                        uiState.value = SignUpUIState.Idle
                    }
                    is Resource.Error -> {
                        _eventFlow.emit(SignUpUIEvent.ShowToast(it.message ?: "알 수 없는 오류가 발생했어요."))
                        uiState.value = SignUpUIState.Error(it.message)
                    }
                    is Resource.Loading -> {
                        uiState.value = SignUpUIState.Loading
                    }
                }
            }
        }
    }
}

@Stable
sealed interface SignUpUIState {

    data object Idle : SignUpUIState

    data object Loading : SignUpUIState

    data class Error(val message: String?) : SignUpUIState
}

sealed interface SignUpUIEvent {
    data object NavigateToNext: SignUpUIEvent
    data class ShowToast(val message: String): SignUpUIEvent
}
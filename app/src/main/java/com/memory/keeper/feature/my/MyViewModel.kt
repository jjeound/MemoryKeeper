package com.memory.keeper.feature.my

import androidx.compose.runtime.Stable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.memory.keeper.core.Resource
import com.memory.keeper.data.repository.SignUpRepository
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
class MyViewModel @Inject constructor(
    private val signUpRepository: SignUpRepository
): ViewModel(){
    val uiState: MutableStateFlow<MyUIState> =
        MutableStateFlow(MyUIState.Idle)

    private val _eventFlow: MutableSharedFlow<MyUIEvent> = MutableSharedFlow()
    val eventFlow = _eventFlow.asSharedFlow()

    private val _userName = MutableStateFlow<String?>(null)
    val userName: StateFlow<String?> = _userName.asStateFlow()

    init {
        getUserName()
    }

    fun logout(){
        viewModelScope.launch {
            signUpRepository.logout().collectLatest { it ->
                when (it) {
                    is Resource.Success -> {
                        _eventFlow.emit(MyUIEvent.NavigateToLogin)
                        uiState.value = MyUIState.Idle
                    }
                    is Resource.Error -> {
                        uiState.value = MyUIState.Error(it.message)
                        _eventFlow.emit(MyUIEvent.ShowToast(it.message ?: "알 수 없는 오류가 발생했어요."))
                    }
                    is Resource.Loading -> {
                        uiState.value = MyUIState.Loading
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
                        _eventFlow.emit(MyUIEvent.ShowToast(it.data ?: "요청이 완료되었어요."))
                        uiState.value = MyUIState.Idle
                    }
                    is Resource.Error -> {
                        _eventFlow.emit(MyUIEvent.ShowToast(it.message ?: "알 수 없는 오류가 발생했어요."))
                        uiState.value = MyUIState.Error(it.message)
                    }
                    is Resource.Loading -> {
                        uiState.value = MyUIState.Loading
                    }
                }
            }
        }
    }

    fun getUserName() {
        viewModelScope.launch {
            _userName.update { signUpRepository.getUserName() }
        }
    }
}

@Stable
sealed interface MyUIState {
    data object Idle : MyUIState
    data object Loading : MyUIState
    data class Error(val message: String?) : MyUIState
}

sealed interface MyUIEvent {
    data object NavigateToLogin: MyUIEvent
    data class ShowToast(val message: String): MyUIEvent
}
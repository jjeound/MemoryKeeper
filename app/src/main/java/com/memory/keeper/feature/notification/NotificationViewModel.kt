package com.memory.keeper.feature.notification

import androidx.compose.runtime.Stable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.memory.keeper.core.Resource
import com.memory.keeper.data.dto.response.Notification
import com.memory.keeper.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@HiltViewModel
class NotificationViewModel @Inject constructor(
    private val userRepository: UserRepository
): ViewModel() {
    val uiState: MutableStateFlow<NotificationUIState> =
        MutableStateFlow(NotificationUIState.Loading)

    private val _notifications: MutableStateFlow<List<Notification>> = MutableStateFlow(emptyList())
    val notifications: StateFlow<List<Notification>> = _notifications.asStateFlow()

    private val _eventFlow: MutableSharedFlow<NotificationUIEvent> = MutableSharedFlow()
    val eventFlow = _eventFlow.asSharedFlow()

    init {
        getNotifications()
    }

    fun getNotifications() {
        viewModelScope.launch {
            userRepository.getNotifications().collectLatest {
                when(it) {
                    is Resource.Success -> {
                        _notifications.value = it.data ?: emptyList()
                        uiState.value = NotificationUIState.Idle
                    }
                    is Resource.Error -> {
                        _eventFlow.emit(NotificationUIEvent.ShowToast(it.message ?: "알 수 없는 오류가 발생했어요."))
                        uiState.value = NotificationUIState.Error(it.message)
                    }
                    is Resource.Loading -> {
                        uiState.value = NotificationUIState.Loading
                    }
                }
            }
        }
    }

    fun onResponseRelationship(
        requestId: Long,
        status: String
    ) {
        viewModelScope.launch {
            userRepository.respondRelationship(requestId, status).collectLatest {
                when(it) {
                    is Resource.Success -> {
                        getNotifications()
                    }
                    is Resource.Error -> {
                        _eventFlow.emit(NotificationUIEvent.ShowToast(it.message ?: "알 수 없는 오류가 발생했어요."))
                        uiState.value = NotificationUIState.Error(it.message)
                    }
                    is Resource.Loading -> {
                        uiState.value = NotificationUIState.Loading
                    }
                }
            }
        }
    }
}

@Stable
sealed interface NotificationUIState {
    data object Idle : NotificationUIState
    data object Loading : NotificationUIState
    data class Error(val message: String?) : NotificationUIState
}

sealed interface NotificationUIEvent {
    data class ShowToast(val message: String): NotificationUIEvent
}
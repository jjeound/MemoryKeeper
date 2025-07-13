package com.memory.keeper.feature.record

import androidx.compose.runtime.Stable
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.memory.keeper.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RecordViewModel @Inject constructor(
    private val userRepository: UserRepository
): ViewModel() {
    val uiState: MutableStateFlow<RecordUIState> =
        MutableStateFlow(RecordUIState.Idle)

    private val _eventFlow: MutableSharedFlow<RecordUIEvent> = MutableSharedFlow()
    val eventFlow = _eventFlow.asSharedFlow()

    private val _role = MutableStateFlow<String?>(null)
    val role: StateFlow<String?> = _role.asStateFlow()

    private val _userName = MutableStateFlow<String?>(null)
    val userName: StateFlow<String?> = _userName.asStateFlow()

    init {
        getMyRole()
        getUserName()
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
sealed interface RecordUIState {
    data object Idle : RecordUIState
    data object Loading : RecordUIState
    data class Error(val message: String?) : RecordUIState
}

sealed interface RecordUIEvent {
    data class ShowToast(val message: String): RecordUIEvent
}
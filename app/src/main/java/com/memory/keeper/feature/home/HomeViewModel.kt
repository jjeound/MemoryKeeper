package com.memory.keeper.feature.home

import android.util.Log
import androidx.compose.runtime.Stable
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.memory.keeper.core.Resource
import com.memory.keeper.data.repository.AIRepository
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
class HomeViewModel @Inject constructor(
    private val aiRepository: AIRepository,
    private val userRepository: UserRepository
): ViewModel() {
    val uiState: MutableStateFlow<HomeUIState> = MutableStateFlow(HomeUIState.Idle)

    private val _eventFlow: MutableSharedFlow<HomeUIEvent> = MutableSharedFlow()
    val eventFlow = _eventFlow.asSharedFlow()

    private val _aiResponse: MutableStateFlow<String?> = MutableStateFlow(null)
    val aiResponse: StateFlow<String?> = _aiResponse.asStateFlow()

    private val _role = MutableStateFlow<String?>(null)
    val role: StateFlow<String?> = _role.asStateFlow()

    private val _userName = MutableStateFlow<String?>(null)
    val userName: StateFlow<String?> = _userName.asStateFlow()

    init {
        getMyRole()
        getUserName()
    }

    fun startChat(userPrompt: String) {
        viewModelScope.launch {
            aiRepository.startChat(userPrompt).collectLatest { it ->
                when (it) {
                    is Resource.Success -> {
                        _aiResponse.value = it.data?.message
                        uiState.value = HomeUIState.Idle
                    }
                    is Resource.Error -> {
                        _aiResponse.value = it.message
                    }
                    is Resource.Loading -> {
                        uiState.value = HomeUIState.Loading
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
sealed interface HomeUIState {
    data object Idle : HomeUIState
    data object Loading : HomeUIState
    data class Error(val message: String?) : HomeUIState
}

sealed interface HomeUIEvent {
    data object ImageGenerated: HomeUIEvent
    data class ShowToast(val message: String): HomeUIEvent
}
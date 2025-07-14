package com.memory.keeper.feature.home

import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.memory.keeper.core.Resource
import com.memory.keeper.data.dto.response.DailyResponse
import com.memory.keeper.data.dto.response.MonthlyResponse
import com.memory.keeper.data.repository.AIRepository
import com.memory.keeper.data.repository.UserRepository
import com.memory.keeper.feature.prompt.PromptUIEvent
import com.memory.keeper.feature.prompt.PromptUIState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale
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

    private val _monthlyResponse: MutableStateFlow<List<MonthlyResponse>> = MutableStateFlow(emptyList())
    val monthlyResponse: StateFlow<List<MonthlyResponse>> = _monthlyResponse.asStateFlow()

    private val _dailyResponse: MutableStateFlow<DailyResponse?> = MutableStateFlow(null)
    val dailyResponse: StateFlow<DailyResponse?> = _dailyResponse.asStateFlow()

    private val _role = MutableStateFlow<String?>(null)
    val role: StateFlow<String?> = _role.asStateFlow()

    private val _userName = MutableStateFlow<String?>(null)
    val userName: StateFlow<String?> = _userName.asStateFlow()

    private val _patientIds: MutableStateFlow<List<Long>> = MutableStateFlow(emptyList())
    val patientIds: StateFlow<List<Long>> = _patientIds.asStateFlow()

    private val _patientNames: MutableStateFlow<List<String>> = MutableStateFlow(emptyList())
    val patientNames: StateFlow<List<String>> = _patientNames.asStateFlow()

    private val _selectedUserId = MutableStateFlow<Long?>(null)
    val selectedUserId: StateFlow<Long?> = _selectedUserId.asStateFlow()

    init {
        getMyRole()
        getMonthlyRecord(LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM")))
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
                            _selectedUserId.value = it.data.firstOrNull()?.userId
                        } else {
                            uiState.value = HomeUIState.Idle
                        }
                    }
                    is Resource.Error -> {
                        uiState.value = HomeUIState.Error(it.message)
                        _eventFlow.emit(HomeUIEvent.ShowToast(it.message ?: "알 수 없는 오류가 발생했어요."))
                    }
                    is Resource.Loading -> {
                        uiState.value = HomeUIState.Loading
                    }
                }
            }
        }
    }

    fun getMonthlyRecord(date: String){
        viewModelScope.launch {
            userRepository.getMonthlyRecord(date).collectLatest {
                when(it){
                    is Resource.Success -> {
                        _monthlyResponse.value = it.data ?: emptyList()
                        uiState.value = HomeUIState.Idle
                    }
                    is Resource.Error -> {
                        uiState.value = HomeUIState.Error(it.message)
                    }
                    is Resource.Loading -> {
                        uiState.value = HomeUIState.Loading
                    }
                }
            }
        }
    }

    fun getDailyRecord(date: String, userId: Long){
        viewModelScope.launch {
            userRepository.getDailyRecord(date, userId).collectLatest {
                when(it){
                    is Resource.Success -> {
                        _dailyResponse.value = it.data
                        uiState.value = HomeUIState.Idle
                    }
                    is Resource.Error -> {
                        uiState.value = HomeUIState.Error(it.message)
                    }
                    is Resource.Loading -> {
                        uiState.value = HomeUIState.Loading
                    }
                }
            }
        }
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

    fun setSelectedUserId(index: Int) {
        _selectedUserId.value = patientIds.value[index]
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
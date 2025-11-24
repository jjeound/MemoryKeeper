package com.memory.keeper.feature.chat

import androidx.compose.runtime.Stable
import androidx.compose.runtime.mutableIntStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.memory.keeper.core.Resource
import com.memory.keeper.data.dto.response.Topic
import com.memory.keeper.data.dto.response.UserInfoPhoto
import com.memory.keeper.data.repository.ChatRepository
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
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val chatRepository: ChatRepository,
    private val userRepository: UserRepository
): ViewModel() {
    val uiState: MutableStateFlow<ChatUiState> = MutableStateFlow(ChatUiState.Idle)

    private val _eventFlow: MutableSharedFlow<ChatUiEvent> = MutableSharedFlow()
    val eventFlow = _eventFlow.asSharedFlow()

    private val _aiResponse: MutableStateFlow<String?> = MutableStateFlow(null)
    val aiResponse: StateFlow<String?> = _aiResponse.asStateFlow()

    private val _topics: MutableStateFlow<List<Topic>> = MutableStateFlow(emptyList())
    val topics: StateFlow<List<Topic>> = _topics.asStateFlow()

    private val _aiImage: MutableStateFlow<List<String>> = MutableStateFlow(emptyList())
    val aiImage: StateFlow<List<String>> = _aiImage.asStateFlow()

    private val _role = MutableStateFlow<String?>(null)
    val role: StateFlow<String?> = _role.asStateFlow()

    private val _userName = MutableStateFlow<String?>(null)
    val userName: StateFlow<String?> = _userName.asStateFlow()

    private val _userId = MutableStateFlow<Long?>(null)
    val userId: StateFlow<Long?> = _userId.asStateFlow()

    private val _userInfoPhoto: MutableStateFlow<List<UserInfoPhoto>> = MutableStateFlow(emptyList())
    val userInfoPhoto: StateFlow<List<UserInfoPhoto>> = _userInfoPhoto.asStateFlow()

    var steps = mutableIntStateOf(0)
        private set

    init {
        getTopics()
        getUserId()
    }

    fun startChat(userPrompt: String, topic: String) {
        viewModelScope.launch {
            chatRepository.startChat(userPrompt, topic).collectLatest {
                when (it) {
                    is Resource.Success -> {
                        _aiResponse.value = it.data?.message
                        uiState.value = ChatUiState.Idle
                    }
                    is Resource.Error -> {
                        uiState.value = ChatUiState.Error(it.message)
                        _eventFlow.emit(ChatUiEvent.ShowToast(it.message ?: "알 수 없는 오류가 발생했어요."))
                    }
                    is Resource.Loading -> {
                        uiState.value = ChatUiState.Loading
                    }
                }
            }
        }
    }

    fun getTopics(){
        viewModelScope.launch {
            chatRepository.getTopics().collectLatest {
                when (it) {
                    is Resource.Success -> {
                        _topics.value = it.data ?: emptyList()
                        uiState.value = ChatUiState.Idle
                    }
                    is Resource.Error -> {
                        _eventFlow.emit(ChatUiEvent.ShowToast(it.message ?: "알 수 없는 오류가 발생했어요."))
                    }
                    is Resource.Loading -> {
                        uiState.value = ChatUiState.Loading
                    }
                }
            }
        }
    }

    fun generateAIImages(dailyId: Long, images: List<String>) {
        viewModelScope.launch {
            chatRepository.generateAIImages(dailyId, images).collectLatest {
                when (it) {
                    is Resource.Success -> {
                        _aiImage.value = it.data ?: emptyList()
                        uiState.value = ChatUiState.Idle
                        _eventFlow.emit(ChatUiEvent.ImageGenerated)
                    }
                    is Resource.Error -> {
                        setSteps(4)
                        _eventFlow.emit(ChatUiEvent.ShowToast(it.message ?: "알 수 없는 오류가 발생했어요."))
                    }
                    is Resource.Loading -> {
                        uiState.value = ChatUiState.Loading
                    }
                }
            }
        }
    }

    fun saveConversation(isConfirmed: Boolean, index: Int) {
        viewModelScope.launch {
            val date = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
            chatRepository.saveConversation(date).collectLatest {
                when (it) {
                    is Resource.Success -> {
                        it.data?.let { id ->
                            if(isConfirmed){
                                val images = listOf(_topics.value[index].url) + _userInfoPhoto.value.map { photo -> photo.url }
                                generateAIImages(id, images)
                            } else {
                                uiState.value = ChatUiState.Idle
                            }
                        }
                    }
                    is Resource.Error -> {
                        _eventFlow.emit(ChatUiEvent.ShowToast(it.message ?: "알 수 없는 오류가 발생했어요."))
                    }
                    is Resource.Loading -> {
                        uiState.value = ChatUiState.Loading
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
            userRepository.getUserId()?.let {
                getUserInfoPhotos(it)
            }
        }
    }

    fun setSteps(step: Int) {
        steps.intValue = step
    }

    fun getUserInfoPhotos(id: Long){
        viewModelScope.launch {
            userRepository.getUserInfoPhotos(id).collectLatest {
                when (it) {
                    is Resource.Success -> {
                        _userInfoPhoto.value = it.data ?: emptyList()
                    }
                    is Resource.Error -> {

                    }
                    is Resource.Loading -> {
                        //uiState.value = PromptUIState.Loading
                    }
                }
            }
        }
    }
}

@Stable
sealed interface ChatUiState {
    data object Idle : ChatUiState
    data object Loading : ChatUiState
    data class Error(val message: String?) : ChatUiState
}

sealed interface ChatUiEvent {
    data object ImageGenerated: ChatUiEvent
    data class ShowToast(val message: String): ChatUiEvent
}


enum class AIState {
    SPEAKING, LISTENING, GENERATING, IDLE
}
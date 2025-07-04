package com.memory.keeper.feature.login

import androidx.compose.runtime.Stable
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject

@HiltViewModel
class SignUpViewModel @Inject constructor(): ViewModel() {
    val uiState: MutableStateFlow<SignUpUiState> =
        MutableStateFlow(SignUpUiState.Loading)


}

@Stable
sealed interface SignUpUiState {

    data object Idle : SignUpUiState

    data object Loading : SignUpUiState

    data class Error(val message: String?) : SignUpUiState
}
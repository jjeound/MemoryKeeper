package com.memory.keeper.feature.record

import androidx.compose.runtime.Stable
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject

@HiltViewModel
class RecordViewModel @Inject constructor(

): ViewModel() {
    val uiState = MutableStateFlow<RecordUiState>(RecordUiState.Idle)
}

@Stable
sealed interface RecordUiState {
    data object Idle : RecordUiState
    data object Loading : RecordUiState
    data class Error(val message: String) : RecordUiState
}
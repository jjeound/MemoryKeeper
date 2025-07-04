package com.memory.keeper.feature.detail

import androidx.compose.runtime.Stable
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.memory.keeper.core.Resource
import com.memory.keeper.data.dto.NewsInfo
import com.memory.keeper.data.repository.DetailRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class DetailViewModel @Inject constructor(
    private val repository: DetailRepository,
    savedStateHandle: SavedStateHandle,
): ViewModel() {
    internal val uiState: MutableStateFlow<DetailsUiState> =
        MutableStateFlow(DetailsUiState.Loading)

    val id = savedStateHandle.getStateFlow<String?>("id", null)
    @OptIn(ExperimentalCoroutinesApi::class)
    val newsDetail: StateFlow<NewsInfo?> =
        id.filterNotNull().flatMapLatest { id ->
            repository.fetchNewsDetail(
                id = id
            ).map {
                when(it){
                    is Resource.Success -> {
                        uiState.tryEmit(DetailsUiState.Idle)
                        it.data
                    }
                    is Resource.Error -> {
                        uiState.tryEmit(DetailsUiState.Error(it.message))
                        null
                    }
                    is Resource.Loading -> {
                        uiState.tryEmit(DetailsUiState.Loading)
                        null
                    }
                }
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = null,
        )

}

@Stable
internal sealed interface DetailsUiState {

    data object Idle : DetailsUiState

    data object Loading : DetailsUiState

    data class Error(val message: String?) : DetailsUiState
}
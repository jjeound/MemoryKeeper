package com.memory.keeper.feature.home

import androidx.compose.runtime.Stable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.memory.keeper.core.Resource
import com.memory.keeper.data.model.News
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: HomeRepository
): ViewModel() {

    internal val hotNewsUiState = MutableStateFlow<HomeUiState>(HomeUiState.Loading)
    internal val latestNewsUiState = MutableStateFlow<HomeUiState>(HomeUiState.Loading)

    val hotNewsFetchingIndex: MutableStateFlow<Int> = MutableStateFlow(1)
    val latestNewsFetchingIndex: MutableStateFlow<Int> = MutableStateFlow(1)

    @OptIn(ExperimentalCoroutinesApi::class)
    val hotNewsList: StateFlow<List<News>> = hotNewsFetchingIndex.flatMapLatest { page ->
        repository.fetchHotNews(
            page = page,
            limit = PAGE_SIZE,
        ).map { resource ->
            when (resource) {
                is Resource.Success -> {
                    hotNewsUiState.tryEmit(HomeUiState.Idle)
                    resource.data ?: emptyList()
                }
                is Resource.Error -> {
                    hotNewsUiState.tryEmit(HomeUiState.Error(resource.message))
                    emptyList()
                }
                is Resource.Loading -> {
                    hotNewsUiState.tryEmit(HomeUiState.Loading)
                    emptyList()
                }
            }
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = emptyList(),
    )

    @OptIn(ExperimentalCoroutinesApi::class)
    val latestNewsList: StateFlow<List<News>> = latestNewsFetchingIndex.flatMapLatest { page ->
        repository.fetchLatestNews(
            page = page,
            limit = PAGE_SIZE,
        ).map { resource ->
            when (resource) {
                is Resource.Success -> {
                    latestNewsUiState.tryEmit(HomeUiState.Idle)
                    resource.data ?: emptyList()
                }
                is Resource.Error -> {
                    latestNewsUiState.tryEmit(HomeUiState.Error(resource.message))
                    emptyList()
                }
                is Resource.Loading -> {
                    latestNewsUiState.tryEmit(HomeUiState.Loading)
                    emptyList()
                }
            }
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = emptyList(),
    )

    fun fetchNextHotNewsList() {
        if (hotNewsUiState.value != HomeUiState.Loading) {
            hotNewsFetchingIndex.value++
        }
    }

    fun fetchNextLatestNewsList() {
        if (latestNewsUiState.value != HomeUiState.Loading) {
            latestNewsFetchingIndex.value++
        }
    }

    fun fetchPrevHotNewsList() {
        if (hotNewsUiState.value != HomeUiState.Loading) {
            hotNewsFetchingIndex.value--
        }
    }

    fun fetchPrevLatestNewsList() {
        if (latestNewsUiState.value != HomeUiState.Loading) {
            latestNewsFetchingIndex.value--
        }
    }

    fun fetchThisHotNewsList(page: Int) {
        if (hotNewsUiState.value != HomeUiState.Loading) {
            hotNewsFetchingIndex.value = page
        }
    }

    fun fetchThisLatestNewsList(page: Int) {
        if (latestNewsUiState.value != HomeUiState.Loading) {
            latestNewsFetchingIndex.value = page
        }
    }
    companion object {
        const val PAGE_SIZE = 5
    }
}

@Stable
internal interface HomeUiState {

    data object Idle : HomeUiState

    data object Loading : HomeUiState

    data class Error(val message: String?) : HomeUiState
}
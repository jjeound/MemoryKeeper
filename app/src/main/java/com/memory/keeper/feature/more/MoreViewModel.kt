package com.memory.keeper.feature.more

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import com.memory.keeper.data.database.entity.mapper.asHotDomain
import com.memory.keeper.data.database.entity.mapper.asLatestDomain
import com.memory.keeper.data.model.News
import com.memory.keeper.data.repository.MoreRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class MoreViewModel @Inject constructor(
    private val repository: MoreRepository,
): ViewModel() {

    private val _newsList = MutableStateFlow<PagingData<News>>(PagingData.empty())
    val newsList: StateFlow<PagingData<News>> = _newsList.asStateFlow()

    fun fetchNewsList(
        isHot: Boolean,
        category: String? = null,
    ) {
        viewModelScope.launch {
            if (isHot){
                repository.fetchHotNews(category).map {
                    it.map { it.asHotDomain() }
                }
            } else {
                repository.fetchLatestNews(category).map {
                    it.map { it.asLatestDomain() }
                }
            }.cachedIn(viewModelScope).collectLatest {
                _newsList.value = it
            }
        }
    }
}
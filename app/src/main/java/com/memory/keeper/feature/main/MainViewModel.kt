package com.memory.keeper.feature.main

import androidx.lifecycle.ViewModel
import com.memory.keeper.navigation.Graph
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

@HiltViewModel
class MainViewModel @Inject constructor(

): ViewModel() {

    private val _startDestination = MutableStateFlow<Graph>(Graph.SignUpGraph)
    val startDestination = _startDestination.asStateFlow()


}
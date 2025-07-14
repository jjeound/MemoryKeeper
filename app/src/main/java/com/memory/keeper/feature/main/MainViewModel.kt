package com.memory.keeper.feature.main

import androidx.lifecycle.ViewModel
import com.memory.keeper.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val userRepository: UserRepository
): ViewModel() {

}
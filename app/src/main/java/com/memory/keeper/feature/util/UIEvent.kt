package com.memory.keeper.feature.util

sealed class UiEvent{
    data class ShowSnackbar(val message: String) : UiEvent()
}
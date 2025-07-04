package com.memory.keeper.navigation

import kotlinx.serialization.Serializable

sealed interface Screen{
    @Serializable
    data object Home: Screen
    @Serializable
    data class More(val isHot: Boolean): Screen
    @Serializable
    data class Detail(val id: String): Screen
    @Serializable
    data object Setting: Screen
    @Serializable
    data class SettingDetail(val order: Int): Screen
    @Serializable
    data object Goal: Screen
    @Serializable
    data object Report: Screen
    @Serializable
    data object My: Screen
}

sealed interface Graph {
    @Serializable
    data object HomeGraph : Graph
    @Serializable
    data object GoalGraph : Graph
    @Serializable
    data object ReportGraph : Graph
    @Serializable
    data object MyGraph : Graph
}
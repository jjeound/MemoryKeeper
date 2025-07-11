package com.memory.keeper.navigation

import kotlinx.serialization.Serializable

sealed interface Screen{
    @Serializable
    data object Home: Screen
    @Serializable
    data object Report: Screen
    @Serializable
    data object My: Screen
    @Serializable
    data object SignUp: Screen
    @Serializable
    data object SelectMode: Screen
    @Serializable
    data object SetName: Screen
    @Serializable
    data class SetRelation(val name: String, val userName: String): Screen
}

sealed interface Graph {
    @Serializable
    data object HomeGraph : Graph
    @Serializable
    data object ReportGraph : Graph
    @Serializable
    data object MyGraph : Graph
    @Serializable
    data object SignUpGraph : Graph
}
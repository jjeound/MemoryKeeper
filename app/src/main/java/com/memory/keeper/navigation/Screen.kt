package com.memory.keeper.navigation

import kotlinx.serialization.Serializable

sealed interface Screen{
    @Serializable
    data object Home: Screen
    @Serializable
    data object Record: Screen
    @Serializable
    data object My: Screen
    @Serializable
    data object SignUp: Screen
    @Serializable
    data class SelectMode(val name: String): Screen
    @Serializable
    data class SearchUser(val name: String): Screen
    @Serializable
    data class SetRelation(val name: String, val userName: String, val userId: Long): Screen
    @Serializable
    data class SignUpFinish(val name: String): Screen
    @Serializable
    data object Prompt: Screen
    @Serializable
    data object Notification: Screen
}

sealed interface Graph {
    @Serializable
    data object HomeGraph : Graph
    @Serializable
    data object RecordGraph : Graph
    @Serializable
    data object MyGraph : Graph
    @Serializable
    data object SignUpGraph : Graph
    @Serializable
    data object PromptGraph : Graph
}
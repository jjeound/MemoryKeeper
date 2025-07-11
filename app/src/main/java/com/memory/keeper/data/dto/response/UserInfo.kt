package com.memory.keeper.data.dto.response

import kotlinx.serialization.Serializable

@Serializable
data class UserInfo(
    val name: String,
    val birthYear: Int,
    val role: String,
    val email: String
)

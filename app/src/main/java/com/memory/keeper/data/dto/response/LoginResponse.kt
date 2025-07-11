package com.memory.keeper.data.dto.response

import kotlinx.serialization.Serializable


@Serializable
data class LoginResponse(
    val id: Long,
    val email: String,
    val accessToken: String,
    val refreshToken: String,
    val name: String,
    val nickname: String?,
)

package com.memory.keeper.data.dto.request

import kotlinx.serialization.Serializable

@Serializable
data class RoleRequest(
    val birthYear: Int = 0,
    val role: String
)
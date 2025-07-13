package com.memory.keeper.data.dto.request

import kotlinx.serialization.Serializable

@Serializable
data class EmailRequest(
    val email: String,
)

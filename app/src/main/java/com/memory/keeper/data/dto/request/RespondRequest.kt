package com.memory.keeper.data.dto.request

import kotlinx.serialization.Serializable

@Serializable
data class RespondRequest(
    val requestId: Long,
    val status: String
)

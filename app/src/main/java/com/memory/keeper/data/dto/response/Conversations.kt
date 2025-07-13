package com.memory.keeper.data.dto.response

import kotlinx.serialization.Serializable

@Serializable
data class Conversations(
    val conversations: List<Conversation>,
    val count: Int,
    val success: Boolean
)
package com.memory.keeper.data.dto.response

import kotlinx.serialization.Serializable

@Serializable
data class Conversation(
    val content: String,
    val speaker: String,
    val timestamp: String
)
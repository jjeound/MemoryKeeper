package com.memory.keeper.data.dto.response

data class Conversations(
    val conversations: List<Conversation>,
    val count: Int,
    val success: Boolean
)
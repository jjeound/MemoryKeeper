package com.memory.keeper.data.dto.response

import kotlinx.serialization.Serializable

@Serializable
data class Topic(
    val topic: String,
    val url: String
)

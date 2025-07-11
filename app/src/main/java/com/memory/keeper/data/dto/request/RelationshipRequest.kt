package com.memory.keeper.data.dto.request

import kotlinx.serialization.Serializable

@Serializable
data class RelationshipRequest(
    val receiverId: Long,
    val type: String
)

package com.memory.keeper.data.dto.response

import kotlinx.serialization.Serializable

@Serializable
data class UserSearched(
    val id: Long,
    val name: String,
    val email: String,
    val alreadyRelated: Boolean
)

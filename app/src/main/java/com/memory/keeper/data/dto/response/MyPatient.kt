package com.memory.keeper.data.dto.response

import kotlinx.serialization.Serializable

@Serializable
data class MyPatient(
    val userId: Long,
    val name: String,
    val email: String,
    val role: String,
    val relationshipType: String
)

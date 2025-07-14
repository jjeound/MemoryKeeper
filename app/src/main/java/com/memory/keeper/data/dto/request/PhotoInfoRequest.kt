package com.memory.keeper.data.dto.request

import kotlinx.serialization.Serializable

@Serializable
data class PhotoInfoRequest(
    val description: String,
    val relationToPatient: String,
    val userInfoId: Long,
)
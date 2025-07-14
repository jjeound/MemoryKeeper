package com.memory.keeper.data.dto.request

import kotlinx.serialization.Serializable

@Serializable
data class UpdatePhotoInfoRequest(
    val description: String,
    val relationToPatient: String
)
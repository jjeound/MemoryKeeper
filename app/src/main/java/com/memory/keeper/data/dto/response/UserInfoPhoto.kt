package com.memory.keeper.data.dto.response

import kotlinx.serialization.Serializable

@Serializable
data class UserInfoPhoto(
    val description: String,
    val fileSizeMb: String,
    val id: Long,
    val lastModifiedBy: String,
    val originalFileName: String,
    val relationToPatient: String,
    val url: String,
    val uuid: String
)
package com.memory.keeper.data.dto.response

import kotlinx.serialization.Serializable

@Serializable
data class UserInfoDetail(
    val age: Int? = null,
    val cognitiveStatus: String? = null,
    val education: String? = null,
    val familyInfo: String? = null,
    val forbiddenKeywords: String? = null,
    val gender: String? = null,
    val hometown: String? = null,
    val lastModifiedBy: String? = null,
    val lifeHistory: String? = null,
    val lifetimeline: String? = null,
    val occupation: String? = null,
)
package com.memory.keeper.data.dto.request

data class UserInfoRequest(
    val age: Int,
    val cognitiveStatus: String,
    val education: String,
    val familyInfo: String,
    val forbiddenKeywords: String,
    val gender: String,
    val hometown: String,
    val lifeHistory: String,
    val lifetimeline: String,
    val occupation: String
)
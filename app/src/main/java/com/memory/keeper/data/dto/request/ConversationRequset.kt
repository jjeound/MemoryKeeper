package com.memory.keeper.data.dto.request

import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable

@Serializable
data class ConversationRequest(
    @SerializedName("user_id")val userId: String,
    val date: String, // "YYYY-MM-DD" 형식의 날짜 문자열
)

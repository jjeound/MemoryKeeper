package com.memory.keeper.data.dto.response

import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable

@Serializable
data class AIChatResponse(
    val message: String,
    val evaluation: String,//평가LLM의 평가 결과
    val topic: String,
    @SerializedName("_ok") val ok: Boolean,
)
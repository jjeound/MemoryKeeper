package com.memory.keeper.data.dto.response

import kotlinx.serialization.Serializable

@Serializable
data class Notification(
    val requestId: Long,
    val senderId: Long,
    val senderName: String,
    val status: String //요청 상태 (PENDING,ACCEPTED,REJECTED)-대기,수락,거절
)

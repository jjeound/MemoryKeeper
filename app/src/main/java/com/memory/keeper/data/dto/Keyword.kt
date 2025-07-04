package com.memory.keeper.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class Keyword(
    val score: Double,
    val word: String
)
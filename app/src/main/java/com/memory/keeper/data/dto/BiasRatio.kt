package com.memory.keeper.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class BiasRatio(
    val left: Double,
    val center: Double,
    val right: Double
)
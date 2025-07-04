package com.memory.keeper.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class NewsResponse(
    val clusters: List<Cluster>,
    val pagination: Pagination
)
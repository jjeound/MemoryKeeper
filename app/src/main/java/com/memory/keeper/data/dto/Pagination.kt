package com.memory.keeper.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class Pagination(
    val limit: Int,
    val page: Int,
    val pages: Int,
    val total: Int
)
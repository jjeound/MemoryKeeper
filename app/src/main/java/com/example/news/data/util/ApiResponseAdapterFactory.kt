package com.example.news.data.util

import com.example.news.data.dto.ApiResponse
import com.google.gson.Gson
import com.google.gson.TypeAdapter
import com.google.gson.TypeAdapterFactory
import com.google.gson.reflect.TypeToken
import java.lang.reflect.ParameterizedType

class ApiResponseAdapterFactory : TypeAdapterFactory {
    override fun <T> create(gson: Gson, type: TypeToken<T>): TypeAdapter<T>? {
        val rawType = type.rawType

        // ApiResponse<T> 타입만 처리
        if (rawType != ApiResponse::class.java) return null

        val resultType = (type.type as? ParameterizedType)?.actualTypeArguments?.firstOrNull()
            ?: throw IllegalArgumentException("ApiResponse must be parameterized")

        val resultAdapter = gson.getAdapter(TypeToken.get(resultType))

        @Suppress("UNCHECKED_CAST")
        return ApiResponseTypeAdapter(resultAdapter) as TypeAdapter<T>
    }
}
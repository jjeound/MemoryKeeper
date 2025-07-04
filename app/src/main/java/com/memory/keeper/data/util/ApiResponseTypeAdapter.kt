package com.memory.keeper.data.util

import com.memory.keeper.data.dto.ApiResponse
import com.google.gson.JsonParser
import com.google.gson.TypeAdapter
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonWriter


class ApiResponseTypeAdapter<T>(
    private val resultAdapter: TypeAdapter<T>
) : TypeAdapter<ApiResponse<T>>() {
    override fun write(
        out: JsonWriter?,
        value: ApiResponse<T>?
    ) {
        throw UnsupportedOperationException("Serialization not supported")
    }

    override fun read(`in`: JsonReader?): ApiResponse<T>? {
        val jsonObject = JsonParser.parseReader(`in`).asJsonObject

        val isSuccess = jsonObject["isSuccess"]?.asBoolean == true
        val code = jsonObject["code"]?.asString ?: ""
        val message = jsonObject["message"]?.asString ?: ""

        val resultJson = jsonObject["result"]
        val result = if (resultJson != null && !resultJson.isJsonNull) {
            resultAdapter.fromJsonTree(resultJson)
        } else {
            null
        }

        return ApiResponse(isSuccess, code, message, result)
    }
}
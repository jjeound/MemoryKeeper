package com.memory.keeper.data.util

import com.kakao.sdk.common.Constants.AUTHORIZATION
import com.memory.keeper.data.repository.TokenRepository
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.Protocol
import okhttp3.Request
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody
import javax.inject.Inject

class AuthInterceptor @Inject constructor(
    private val tokenRepository: TokenRepository,
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val token: String? = runBlocking {
            tokenRepository.getAccessToken()
        }

        if (token.isNullOrEmpty()) {
            return errorResponse(chain.request())
        }

        val request = chain.request().newBuilder().header(AUTHORIZATION, token).build()

        return chain.proceed(request)
    }

    private fun errorResponse(request: Request): Response {
        val emptyBody = "".toResponseBody("application/json".toMediaTypeOrNull())
        //AuthEventBus.send(AuthEvent.Logout)
        return Response.Builder()
            .request(request)
            .protocol(Protocol.HTTP_2)
            .code(401)
            .message("Unauthorized")
            .body(emptyBody)
            .build()
    }
}
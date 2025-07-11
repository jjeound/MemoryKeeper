package com.memory.keeper.data.util

import com.kakao.sdk.common.Constants.AUTHORIZATION
import com.memory.keeper.data.repository.TokenRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import javax.inject.Inject

class AuthAuthenticator @Inject constructor(
    private val tokenRepository: TokenRepository
) : Authenticator {
    override fun authenticate(route: Route?, response: Response): Request? {
        if (responseCount(response) >= MAX_AUTH_RETRY) {
            //AuthEventBus.send(AuthEvent.Logout)
            return null // 무한 루프 방지
        }

        if (response.code != 401) return null

        val newToken = runBlocking {
            withContext(Dispatchers.IO) {
                tokenRepository.refreshAndSaveToken()
            }
        } ?: return null

        //AuthEventBus.send(AuthEvent.Login)

        return response.request.newBuilder()
            .header(AUTHORIZATION, newToken.accessToken)
            .build()
    }

    private fun responseCount(response: Response): Int {
        var count = 1
        var priorResponse = response.priorResponse
        while (priorResponse != null) {
            count++
            priorResponse = priorResponse.priorResponse
        }
        return count
    }

    companion object {
        private const val MAX_AUTH_RETRY = 3
    }
}
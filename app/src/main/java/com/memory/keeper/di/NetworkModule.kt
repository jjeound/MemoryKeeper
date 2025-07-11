package com.memory.keeper.di

import com.memory.keeper.data.util.ApiResponseAdapterFactory
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.memory.keeper.core.Constants.BASE_URL
import com.memory.keeper.data.repository.TokenRepository
import com.memory.keeper.data.service.AIService
import com.memory.keeper.data.service.SignUpService
import com.memory.keeper.data.service.TokenService
import com.memory.keeper.data.service.UserService
import com.memory.keeper.data.util.AuthAuthenticator
import com.memory.keeper.data.util.AuthInterceptor
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideGson(): Gson {
        return GsonBuilder()
            .registerTypeAdapterFactory(ApiResponseAdapterFactory())
            .create()
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(tokenRepository: TokenRepository): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(AuthInterceptor(tokenRepository))
            .authenticator(AuthAuthenticator(tokenRepository))
            .connectTimeout(30, TimeUnit.SECONDS)   // 연결 타임아웃
            .readTimeout(30, TimeUnit.SECONDS)      // 응답 타임아웃
            .writeTimeout(30, TimeUnit.SECONDS)     // 요청 타임아웃
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(
        gson: Gson,
        okHttpClient: OkHttpClient
    ): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient) // OkHttpClient 주입
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
    }

    @Provides
    @Singleton
    fun provideAIService(retrofit: Retrofit): AIService {
        return retrofit.create(AIService::class.java)
    }

    @Provides
    @Singleton
    fun provideLoginService(retrofit: Retrofit): SignUpService {
        return retrofit.create(SignUpService::class.java)
    }

    @Provides
    @Singleton
    fun provideTokenService(retrofit: Retrofit): TokenService{
        return retrofit.create(TokenService::class.java)
    }

    @Provides
    @Singleton
    fun provideUserService(retrofit: Retrofit): UserService{
        return retrofit.create(UserService::class.java)
    }
}
package com.memory.keeper.di

import com.memory.keeper.data.service.NewsClient
import com.memory.keeper.data.service.NewsService
import com.memory.keeper.data.util.ApiResponseAdapterFactory
import com.google.gson.Gson
import com.google.gson.GsonBuilder
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
    fun provideOkHttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
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
            .baseUrl("https://newsbiasinsight.netlify.app/")
            .client(okHttpClient) // OkHttpClient 주입
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
    }

    @Provides
    @Singleton
    fun provideNewsService(retrofit: Retrofit): NewsService {
        return retrofit.create(NewsService::class.java)
    }

    @Provides
    @Singleton
    fun provideNewsClient(newsService: NewsService): NewsClient {
        return NewsClient(newsService)
    }
}
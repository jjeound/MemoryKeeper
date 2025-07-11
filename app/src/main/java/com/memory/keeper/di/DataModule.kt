package com.memory.keeper.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
internal interface DataModule {

    @Binds
    fun bindsHomeRepository(homeRepositoryImpl: HomeRepositoryImpl): HomeRepository

    @Binds
    fun bindsMoreRepository(moreRepositoryImpl: MoreRepositoryImpl): MoreRepository

    @Binds
    fun bindsDetailRepository(detailRepositoryImpl: DetailRepositoryImpl): DetailRepository
}

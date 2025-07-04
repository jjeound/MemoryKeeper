package com.memory.keeper.di

import com.memory.keeper.data.repository.DetailRepository
import com.memory.keeper.data.repository.DetailRepositoryImpl
import com.memory.keeper.data.repository.HomeRepository
import com.memory.keeper.data.repository.HomeRepositoryImpl
import com.memory.keeper.data.repository.MoreRepository
import com.memory.keeper.data.repository.MoreRepositoryImpl
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

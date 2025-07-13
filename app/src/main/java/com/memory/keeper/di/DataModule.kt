package com.memory.keeper.di

import com.memory.keeper.data.repository.AIRepository
import com.memory.keeper.data.repository.AIRepositoryImpl
import com.memory.keeper.data.repository.LoginRepository
import com.memory.keeper.data.repository.LoginRepositoryImpl
import com.memory.keeper.data.repository.SignUpRepository
import com.memory.keeper.data.repository.SignUpRepositoryImpl
import com.memory.keeper.data.repository.TokenRepository
import com.memory.keeper.data.repository.TokenRepositoryImpl
import com.memory.keeper.data.repository.UserRepository
import com.memory.keeper.data.repository.UserRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
internal interface DataModule {

    @Binds
    fun bindsTokenRepository(impl: TokenRepositoryImpl):TokenRepository

    @Binds
    fun bindsLoginRepository(impl: LoginRepositoryImpl): LoginRepository

    @Binds
    fun bindsSignUpRepository(impl: SignUpRepositoryImpl): SignUpRepository

    @Binds
    fun bindsUserRepository(impl: UserRepositoryImpl): UserRepository

    @Binds
    fun bindsAIRepository(impl: AIRepositoryImpl): AIRepository
}

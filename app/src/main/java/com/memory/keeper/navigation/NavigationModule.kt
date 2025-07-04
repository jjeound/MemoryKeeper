package com.memory.keeper.navigation

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface NavigationModule {

    @Binds
    @Singleton
    fun provideComposeNavigator(
        composeNavigator: ComposeNavigator,
    ): AppComposeNavigator<Screen>
}
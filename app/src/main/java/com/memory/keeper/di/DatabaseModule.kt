package com.memory.keeper.di

import android.app.Application
import androidx.room.Room
import com.memory.keeper.data.database.remote.LatestNewsDao
import com.memory.keeper.data.database.db.HotNewsDatabase
import com.memory.keeper.data.database.db.LatestNewsDatabase
import com.memory.keeper.data.database.remote.HotNewsDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
internal object DatabaseModule {

    @Provides
    @Singleton
    fun provideHotNewsDatabase(
        application: Application,
    ): HotNewsDatabase {
        return Room
            .databaseBuilder(application, HotNewsDatabase::class.java, "HotNews.db")
            .build()
    }

    @Provides
    @Singleton
    fun provideHotNewsDao(db: HotNewsDatabase): HotNewsDao {
        return db.newsDao()
    }

    @Provides
    @Singleton
    fun provideLatestNewsDatabase(
        application: Application,
    ): LatestNewsDatabase {
        return Room
            .databaseBuilder(application, LatestNewsDatabase::class.java, "LatestNews.db")
            .build()
    }

    @Provides
    @Singleton
    fun provideLatestNewsDao(db: LatestNewsDatabase): LatestNewsDao {
        return db.newsDao()
    }
}
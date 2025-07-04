package com.memory.keeper.data.database.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.memory.keeper.data.database.entity.LatestNewsEntity
import com.memory.keeper.data.database.remote.LatestNewsDao


@Database(
    entities = [LatestNewsEntity::class],
    version = 1,
    exportSchema = true,
)
abstract class LatestNewsDatabase : RoomDatabase() {
    abstract fun newsDao(): LatestNewsDao
}
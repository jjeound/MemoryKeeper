package com.memory.keeper.data.database.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.memory.keeper.data.database.entity.HotNewsEntity
import com.memory.keeper.data.database.remote.HotNewsDao


@Database(
    entities = [HotNewsEntity::class],
    version = 1,
    exportSchema = true,
)
abstract class HotNewsDatabase : RoomDatabase() {
    abstract fun newsDao(): HotNewsDao
}
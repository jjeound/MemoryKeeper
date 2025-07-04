package com.memory.keeper.data.database.remote

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.memory.keeper.data.database.entity.HotNewsEntity

@Dao
interface HotNewsDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNewsList(newsList: List<HotNewsEntity>)

    @Query("SELECT * FROM hotnewsentity WHERE page = :page_")
    suspend fun getNewsList(page_: Int): List<HotNewsEntity>

    @Query("""
    SELECT * FROM hotnewsentity 
    WHERE (:category_ IS NULL OR category = :category_)
""")
    fun getNewsListPaged(category_: String?): PagingSource<Int, HotNewsEntity>

    @Query("DELETE FROM hotnewsentity")
    suspend fun clearAll()
}
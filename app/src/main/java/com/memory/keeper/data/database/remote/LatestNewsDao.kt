package com.memory.keeper.data.database.remote

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.memory.keeper.data.database.entity.LatestNewsEntity

@Dao
interface LatestNewsDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNewsList(newsList: List<LatestNewsEntity>)

    @Query("SELECT * FROM latestnewsentity WHERE page = :page_")
    suspend fun getNewsList(page_: Int): List<LatestNewsEntity>

    @Query("""
    SELECT * FROM latestnewsentity 
    WHERE (:category_ IS NULL OR category = :category_)
""")
    fun getNewsListPaged(category_: String?): PagingSource<Int, LatestNewsEntity>

    @Query("DELETE FROM latestnewsentity")
    suspend fun clearAll()
}
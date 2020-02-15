package com.huy.mappies.db

import androidx.lifecycle.LiveData
import androidx.room.*
import com.huy.mappies.model.Bookmark
import com.huy.mappies.model.BookmarkView

@Dao
interface BookmarkDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(marker: Bookmark): Long

    @Query("SELECT * FROM bookmark ORDER BY name")
    fun getAllBookmarkViews(): LiveData<List<BookmarkView>>

    @Query("SELECT * FROM bookmark WHERE placeId = :placeId")
    suspend fun get(placeId: String): Bookmark

    @Query("SELECT * FROM bookmark WHERE id = :id")
    suspend fun get(id: Long): Bookmark

    @Query("SELECT * FROM bookmark WHERE id = :id")
    fun getBookmarkView(id: Long): LiveData<BookmarkView>

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun update(bookmark: Bookmark)

    @Delete
    suspend fun delete(bookmark: Bookmark)

}
package com.huy.mappies.db

import androidx.lifecycle.LiveData
import androidx.room.*
import com.huy.mappies.model.Bookmark
import com.huy.mappies.model.BookmarkView

@Dao
interface BookmarkDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(marker: Bookmark): Long

    @Query("SELECT * FROM bookmark ORDER BY name")
    fun getAllBookmarkViews(): LiveData<List<BookmarkView>>

    @Query("SELECT * FROM bookmark WHERE placeId = :placeId")
    fun get(placeId: String): Bookmark

    @Query("SELECT * FROM bookmark WHERE id = :id")
    fun get(id: Long): Bookmark

    @Query("SELECT * FROM bookmark WHERE id = :id")
    fun getBookmarkView(id: Long): LiveData<BookmarkView>

    @Query("SELECT * FROM bookmark WHERE id = :id")
    fun getLiveData(id: Long): Bookmark

    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun update(bookmark: Bookmark)

    @Delete
    fun delete(bookmark: Bookmark)

}
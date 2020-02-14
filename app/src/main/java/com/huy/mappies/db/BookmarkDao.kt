package com.huy.mappies.db

import androidx.lifecycle.LiveData
import androidx.room.*
import com.huy.mappies.model.Bookmark
import com.huy.mappies.model.BookmarkView

@Dao
interface BookmarkDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(marker: Bookmark): Long

    @Query("SELECT * FROM bookmark")
    fun getAllBookmarkViews(): LiveData<List<BookmarkView>>

    @Query("SELECT * FROM bookmark WHERE id = :id")
    fun get(id: Long): Bookmark

    @Query("SELECT * FROM bookmark WHERE id = :id")
    fun getBookmarkView(id: Long): LiveData<BookmarkView>

    @Query("SELECT * FROM bookmark WHERE id = :id")
    fun getLiveData(id: Long): Bookmark

    @Update
    fun update(marker: Bookmark)

    @Delete
    fun delete(marker: Bookmark)

}
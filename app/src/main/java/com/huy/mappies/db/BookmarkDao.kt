package com.huy.mappies.db

import androidx.lifecycle.LiveData
import androidx.room.*
import com.huy.mappies.model.Bookmark

@Dao
interface BookmarkDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(marker: Bookmark): Long

    @Query("SELECT * FROM bookmark")
    fun getAll(): LiveData<List<Bookmark>>

    @Query("SELECT * FROM bookmark WHERE id = :id")
    fun get(id: Long): Bookmark

    @Query("SELECT * FROM bookmark WHERE id = :id")
    fun getLiveData(id: Long): Bookmark

    @Update
    fun update(marker: Bookmark)

    @Delete
    fun delete(marker: Bookmark)

}
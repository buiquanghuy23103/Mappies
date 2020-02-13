package com.huy.mappies.db

import androidx.lifecycle.LiveData
import androidx.room.*
import com.huy.mappies.model.Marker

@Dao
interface MarkerDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(marker: Marker): Long

    @Query("SELECT * FROM marker")
    fun getAll(): LiveData<List<Marker>>

    @Query("SELECT * FROM marker WHERE id = :id")
    fun get(id: Long): Marker

    @Query("SELECT * FROM marker WHERE id = :id")
    fun getLiveData(id: Long): Marker

    @Update
    fun update(marker: Marker)

    @Delete
    fun delete(marker: Marker)

}
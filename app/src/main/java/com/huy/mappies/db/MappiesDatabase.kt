package com.huy.mappies.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.huy.mappies.model.Marker

@Database(
    entities = [Marker::class],
    version = 1,
    exportSchema = false
)
abstract class MappiesDatabase: RoomDatabase() {

    abstract fun markerDao(): MarkerDao

    companion object {
        fun create(context: Context): MappiesDatabase {

            return Room.databaseBuilder(
                context.applicationContext,
                MappiesDatabase::class.java,
                "Mappies"
            ).build()


        }
    }

}
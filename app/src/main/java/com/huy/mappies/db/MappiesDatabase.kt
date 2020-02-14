package com.huy.mappies.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.huy.mappies.model.Bookmark

@Database(
    entities = [Bookmark::class],
    version = 2,
    exportSchema = false
)
abstract class MappiesDatabase: RoomDatabase() {

    abstract fun bookmarkDao(): BookmarkDao

    companion object {
        fun create(context: Context): MappiesDatabase {

            return Room.databaseBuilder(
                context.applicationContext,
                MappiesDatabase::class.java,
                "Mappies"
            )
                .fallbackToDestructiveMigration()
                .build()


        }
    }

}
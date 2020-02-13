package com.huy.mappies.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.huy.mappies.model.Marker

@Database(
    entities = [Marker::class],
    version = 1
)
abstract class MappiesDatabase: RoomDatabase() {

    companion object {
        private var instance: MappiesDatabase? = null

        fun getInstance(context: Context): MappiesDatabase {

            if (instance == null) {

                instance = Room.databaseBuilder(
                    context.applicationContext,
                    MappiesDatabase::class.java,
                    "Mappies"
                ).build()
            }

            return instance as MappiesDatabase

        }
    }

}
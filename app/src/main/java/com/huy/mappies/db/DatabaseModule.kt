package com.huy.mappies.db

import android.content.Context
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class DatabaseModule {

    @Singleton
    @Provides
    fun provideDatabase(context: Context) = MappiesDatabase.create(context)

    @Provides
    fun provideMarkerDao(db: MappiesDatabase) = db.markerDao()

}
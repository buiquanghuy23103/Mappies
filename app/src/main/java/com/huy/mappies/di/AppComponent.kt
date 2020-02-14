package com.huy.mappies.di

import android.content.Context
import com.huy.mappies.db.DatabaseModule
import com.huy.mappies.ui.BookmarkDetailsActivity
import com.huy.mappies.ui.MapsActivity
import com.huy.mappies.viewmodel.ViewModelModule
import dagger.BindsInstance
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [DatabaseModule::class, ViewModelModule::class])
interface AppComponent {

    fun inject(activity: MapsActivity)
    fun inject(activity: BookmarkDetailsActivity)

    @Component.Factory
    interface Factory {
        fun create(@BindsInstance context: Context): AppComponent
    }

}
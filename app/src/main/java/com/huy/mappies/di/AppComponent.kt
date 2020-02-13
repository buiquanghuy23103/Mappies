package com.huy.mappies.di

import android.content.Context
import com.huy.mappies.db.DatabaseModule
import dagger.BindsInstance
import dagger.Component

@Component(modules = [DatabaseModule::class])
interface AppComponent {

    @Component.Factory
    interface Factory {
        fun create(@BindsInstance context: Context): AppComponent
    }

}
package com.huy.mappies

import android.app.Application
import com.huy.mappies.di.AppComponent
import com.huy.mappies.di.DaggerAppComponent
import timber.log.Timber

class MainApplication: Application() {
    lateinit var component: AppComponent
    companion object{
        private var INSTANCE: MainApplication? = null

        @JvmStatic
        fun get(): MainApplication = INSTANCE!!
    }
    override fun onCreate() {
        super.onCreate()
        Timber.plant(Timber.DebugTree())
        INSTANCE = this
        component = DaggerAppComponent.factory().create(this)
    }
}
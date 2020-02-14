package com.huy.mappies.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
interface ViewModelModule {

    @Binds
    fun bindViewModelFactory(factory: ViewModelFactory): ViewModelProvider.Factory

    @Binds
    @IntoMap
    @ViewModelKey(MapsViewModel::class)
    fun mapsViewModel(mapsViewModel: MapsViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(BookmarkDetailsViewModel::class)
    fun bookmarkDetailsViewModel(bookmarkDetailsViewModel: BookmarkDetailsViewModel): ViewModel

}
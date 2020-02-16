package com.huy.mappies.viewmodel

import android.content.Context
import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.libraries.places.api.model.Place
import com.huy.mappies.repository.BookmarkRepo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

class MapsViewModel @Inject constructor(
    private val bookmarkRepo: BookmarkRepo,
    private val context: Context
) : ViewModel() {

    val allBookmarkViews = bookmarkRepo.allBookmarkViews


    fun savePlaceInfoToDb(place: Place, image: Bitmap?) {

        viewModelScope.launch(Dispatchers.IO) {
            val bookmark = bookmarkRepo.createBookmark(place)
            val newId = bookmarkRepo.insertBookmarkToDb(bookmark)
            image?.let {
                bookmark.saveImage(it, context)
            }

            Timber.i("New bookmark $newId added to db")
        }

    }




}
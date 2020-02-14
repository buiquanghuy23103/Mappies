package com.huy.mappies.viewmodel

import android.content.Context
import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import com.google.android.libraries.places.api.model.Place
import com.huy.mappies.repository.BookmarkRepo
import timber.log.Timber
import javax.inject.Inject

class MapsViewModel @Inject constructor(
    private val bookmarkRepo: BookmarkRepo,
    private val context: Context
) : ViewModel() {

    val allBookmarkViews = bookmarkRepo.allBookmarkViews

    fun addBookmarkFromPlace(place: Place, image: Bitmap?) {

        val bookmark = bookmarkRepo.createBookmark(place)
        val newId = bookmarkRepo.addBookmark(bookmark)
        image?.let {
            bookmark.saveImage(it, context)
        }

        Timber.i("New bookmark $newId added to db")

    }

}
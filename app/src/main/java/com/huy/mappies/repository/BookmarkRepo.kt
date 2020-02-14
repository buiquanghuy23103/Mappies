package com.huy.mappies.repository

import com.google.android.libraries.places.api.model.Place
import com.huy.mappies.db.BookmarkDao
import com.huy.mappies.model.Bookmark
import javax.inject.Inject

class BookmarkRepo @Inject constructor(
    private val bookmarkDao: BookmarkDao
) {

    fun addBookmark(bookmark: Bookmark): Long? {
        val newId = bookmarkDao.insert(bookmark)
        bookmark.id = newId
        return newId
    }

    fun createBookmark(place: Place): Bookmark {
        return Bookmark(
            placeId = place.id,
            name = place.name.toString(),
            latitude = place.latLng?.latitude ?: 0.0,
            longtitude = place.latLng?.longitude ?: 0.0,
            phone = place.phoneNumber.toString(),
            address = place.address.toString()
        )
    }

    val allBookmarkViews = bookmarkDao.getAllBookmarkViews()

    fun getBookmarkView(id: Long) = bookmarkDao.getBookmarkView(id)

}
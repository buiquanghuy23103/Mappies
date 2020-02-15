package com.huy.mappies.repository

import com.google.android.libraries.places.api.model.Place
import com.huy.mappies.db.BookmarkDao
import com.huy.mappies.model.Bookmark
import com.huy.mappies.utils.OTHER
import com.huy.mappies.utils.buildCategoryToIconMap
import com.huy.mappies.utils.buildPlaceTypeToCategoryMap
import javax.inject.Inject

class BookmarkRepo @Inject constructor(
    private val bookmarkDao: BookmarkDao
) {

    private val placeTypeToCategoryMap = buildPlaceTypeToCategoryMap()
    private val categoryToIconMap = buildCategoryToIconMap()

    fun categoryToIcon(category: String) = categoryToIconMap[category]

    fun placeTypeToCategory(placeType: Place.Type): String {
        return if (placeTypeToCategoryMap.containsKey(placeType))
            placeTypeToCategoryMap[placeType].toString()
        else OTHER
    }

    suspend fun addBookmark(bookmark: Bookmark): Long? {
        val findBookmarkInDb = bookmark.placeId?.let { bookmarkDao.get(it) }
        return if (findBookmarkInDb == null) {
            val newId = bookmarkDao.insert(bookmark)
            bookmark.id = newId
            newId
        } else {
            bookmark.id = findBookmarkInDb.id
            bookmarkDao.update(bookmark)
            bookmark.id
        }
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

    suspend fun getBookmark(id: Long) = bookmarkDao.get(id)

    suspend fun updateBookmark(bookmark: Bookmark) {
        bookmarkDao.update(bookmark)
    }

}
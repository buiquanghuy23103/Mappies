package com.huy.mappies.viewmodel

import androidx.lifecycle.ViewModel
import com.huy.mappies.model.Bookmark
import com.huy.mappies.model.BookmarkView
import com.huy.mappies.repository.BookmarkRepo
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import javax.inject.Inject

class BookmarkDetailsViewModel @Inject constructor(
    private val bookmarkRepo: BookmarkRepo
): ViewModel() {

    fun getBookmarkView(id: Long) = bookmarkRepo.getBookmarkView(id)

    fun updateBookmark(bookmarkView: BookmarkView?) {
        if (bookmarkView == null) return
        GlobalScope.launch {
            val bookmark = getBookmarkFromBookmarkView(bookmarkView)
            bookmark?.let { bookmarkRepo.updateBookmark(it) }
        }
    }

    private suspend fun getBookmarkFromBookmarkView(bookmarkView: BookmarkView): Bookmark? {

        // Load the original bookmark from db to retain the values that aren't updated by
        // the bookmarkView
        val bookmark = bookmarkView.id?.let {
            bookmarkRepo.getBookmark(it)
        }

        return bookmark?.apply {
            id = bookmarkView.id
            name = bookmarkView.name
            phone = bookmarkView.phone
            address = bookmarkView.address
            notes = bookmarkView.notes
            category = bookmarkView.category
        }

    }

}
package com.huy.mappies.viewmodel

import androidx.lifecycle.ViewModel
import com.huy.mappies.repository.BookmarkRepo
import javax.inject.Inject

class BookmarkDetailsViewModel @Inject constructor(
    private val bookmarkRepo: BookmarkRepo
): ViewModel() {

    fun getBookmarkView(id: Long) = bookmarkRepo.getBookmarkView(id)

}
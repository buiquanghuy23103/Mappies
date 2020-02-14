package com.huy.mappies.model

import android.content.Context
import android.graphics.Bitmap
import com.huy.mappies.utils.ImageUtils

data class BookmarkView(
    var id: Long? = null,
    var latitude: Double = 0.0,
    var longtitude: Double = 0.0,
    var name: String = "",
    var phone: String = ""
) {
    fun getImage(context: Context): Bitmap? {
        id?.let {
            val filename = ImageUtils.getImageFilename(it)
            return ImageUtils.loadBitmapFromFile(context, filename)
        }
        return null
    }
}
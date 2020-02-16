package com.huy.mappies.model

import android.content.Context
import android.graphics.Bitmap
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.huy.mappies.utils.ImageUtils

@Entity
data class Bookmark(
    @PrimaryKey(autoGenerate = true)
    var id: Long? = null,

    var placeId: String? = null,
    var name: String = "",
    var address: String = "",
    var latitude: Double = 0.0,
    var longtitude: Double = 0.0,
    var phone: String = "",
    var notes: String = "",
    var category: String = ""
) {

    fun saveImage(image: Bitmap, context: Context) {
        id?.let {
            ImageUtils.saveBitmapToFile(context, image, ImageUtils.getImageFilename(it))
        }
    }

}
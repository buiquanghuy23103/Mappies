package com.huy.mappies.utils

import android.content.Context
import android.graphics.Bitmap
import timber.log.Timber
import java.io.ByteArrayOutputStream

object ImageUtils {
    fun saveBitmapToFile(context: Context, bitmap: Bitmap, filename: String) {

        val bytes = getByteArrayFromBitmap(bitmap)

        try {
            context.openFileOutput(filename, Context.MODE_PRIVATE).also {
                it.write(bytes)
                it.close()
            }
        } catch (error: Exception) {
            Timber.e(error)
        }

    }

    private fun getByteArrayFromBitmap(bitmap: Bitmap): ByteArray {
        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
        return stream.toByteArray()
    }

}
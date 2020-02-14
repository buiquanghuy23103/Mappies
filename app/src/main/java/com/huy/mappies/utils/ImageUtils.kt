package com.huy.mappies.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import timber.log.Timber
import java.io.ByteArrayOutputStream
import java.io.File

object ImageUtils {

    fun getImageFilename(id: Long) = "bookmark${id}.png"

    fun loadBitmapFromFile(context: Context, filename: String): Bitmap? {
        val filePath = File(context.filesDir, filename).absolutePath
        return BitmapFactory.decodeFile(filePath)
    }

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
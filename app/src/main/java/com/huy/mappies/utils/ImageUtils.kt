package com.huy.mappies.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Environment
import timber.log.Timber
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

object ImageUtils {

    @Throws(IOException::class)
    fun getImageFilename(context: Context): File {
        val timeStamp = SimpleDateFormat("yyyyMMddHHmmss", Locale.US).format(Date())
        val filename = "Mappies_${timeStamp}_"
        val filesDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(filename, ".jpg", filesDir)
    }

    fun getImageFilename(id: Long) = "bookmark${id}.png"

    fun loadBitmapFromFile(context: Context, filename: String?): Bitmap? {
        if (filename == null) return null
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
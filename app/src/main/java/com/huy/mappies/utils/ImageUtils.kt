package com.huy.mappies.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Environment
import timber.log.Timber
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.IOException
import java.io.InputStream
import java.text.SimpleDateFormat
import java.util.*

object ImageUtils {

    fun decodeUriStreamToSize(context: Context, uri: Uri, width: Int, height: Int): Bitmap? {

        var inputStream: InputStream? = null

        try {
            inputStream = context.contentResolver.openInputStream(uri)

            if (inputStream != null) {

                val options = BitmapFactory.Options()

                // just load the size to "options" variable, so we turn the "inJustDecodeBounds" on
                options.inJustDecodeBounds = true
                BitmapFactory.decodeStream(inputStream, null, options)
                inputStream.close()

                // load the actual image to inputStream
                inputStream = context.contentResolver.openInputStream(uri)
                if (inputStream != null) {

                    // set new size
                    options.inSampleSize = calculateInSampleSize(
                        options.outWidth, options.outHeight,
                        width, height
                    )

                    // now we want to load the actual image so turn the "inJustDecodeBounds" off
                    options.inJustDecodeBounds = false

                    val bitmap = BitmapFactory.decodeStream(inputStream, null, options)
                    inputStream.close()

                    return bitmap

                }

            }

            return null
        } catch (error: IOException) {
            Timber.e(error)
        } finally {
            inputStream?.close()
        }

        return null

    }

    fun decodeFileToSize(filePath: String, width: Int, height: Int): Bitmap {
        val options = BitmapFactory.Options()

        // just load the size to "options" variable, not the actual image
        options.inJustDecodeBounds = true
        BitmapFactory.decodeFile(filePath, options)

        // set new size
        options.inSampleSize = calculateInSampleSize(
            options.outWidth, options.outHeight, width, height
        )

        // load the actual image
        options.inJustDecodeBounds = false

        return BitmapFactory.decodeFile(filePath, options)
    }

    private fun calculateInSampleSize(width: Int, height: Int, reqWidth: Int, reqHeight: Int): Int {
        var inSampleSize = 1

        if (height > reqHeight || width > reqWidth) {

            while (excessive(height, reqHeight, inSampleSize)
                && excessive(width, reqWidth, inSampleSize))
            {
                inSampleSize *= 2
            }

        }

        return inSampleSize

    }

    private fun excessive(size: Int, reqSize: Int, inSampleSize: Int): Boolean {
        return (size / 2) / inSampleSize >= reqSize
    }

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

    fun saveBitmapToFile(context: Context, bitmap: Bitmap, filename: String?) {

        if (filename == null) return

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
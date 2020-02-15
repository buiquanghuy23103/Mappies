package com.huy.mappies.utils

import android.content.Context
import android.net.Uri
import androidx.core.content.FileProvider
import java.io.File

fun File.getUri(context: Context): Uri? {
    return FileProvider.getUriForFile(context, "com.huy.mappies.fileprovider", this)
}
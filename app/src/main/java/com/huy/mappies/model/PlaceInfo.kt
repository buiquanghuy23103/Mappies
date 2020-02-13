package com.huy.mappies.model

import android.graphics.Bitmap
import com.google.android.libraries.places.api.model.Place

data class PlaceInfo(
    val place: Place? = null,
    val image: Bitmap? = null
)
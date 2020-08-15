package com.huy.mappies.model

import android.graphics.Bitmap
import com.google.android.libraries.places.api.model.Place
import com.huawei.hms.site.api.model.Site

data class PlaceInfo(
    val place: Place? = null,
    val image: Bitmap? = null
)

data class SiteInfo(
    val site: Site? = null,
    val image: Bitmap? = null
)
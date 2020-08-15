package com.huy.mappies.utils

import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.FetchPhotoRequest
import com.huawei.hms.site.api.model.DetailSearchRequest


fun buildPhotoRequest(place: Place, maxWidth: Int?, maxHeight: Int?): FetchPhotoRequest? {
    return place.photoMetadatas?.get(0)?.let {
        FetchPhotoRequest.builder(it)
        .setMaxWidth(maxWidth)
        .setMaxHeight(maxHeight)
        .build()
    }
}

fun buildSiteRequest(pointOfInterest: com.huawei.hms.maps.model.PointOfInterest): DetailSearchRequest {
    return DetailSearchRequest().apply {
        siteId = pointOfInterest.placeId
        language = "en"
    }
}
package com.huy.mappies.utils

import com.google.android.gms.maps.model.PointOfInterest
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.FetchPhotoRequest
import com.google.android.libraries.places.api.net.FetchPlaceRequest


fun buildPlaceRequest(pointOfInterest: PointOfInterest)
        = FetchPlaceRequest.builder(pointOfInterest.placeId, placeFields)
            .build()

fun buildPhotoRequest(place: Place, maxWidth: Int?, maxHeight: Int?): FetchPhotoRequest? {
        return place.photoMetadatas?.get(0)?.let {
            FetchPhotoRequest.builder(it)
            .setMaxWidth(maxWidth)
            .setMaxHeight(maxHeight)
            .build()
        }
}
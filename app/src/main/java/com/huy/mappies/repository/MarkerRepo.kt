package com.huy.mappies.repository

import com.google.android.libraries.places.api.model.Place
import com.huy.mappies.db.MarkerDao
import com.huy.mappies.model.Marker
import javax.inject.Inject

class MarkerRepo @Inject constructor(
    private val markerDao: MarkerDao
) {

    fun addMarker(marker: Marker): Long? {
        val newId = markerDao.insert(marker)
        marker.id = newId
        return newId
    }

    fun createMarker(place: Place): Marker {
        return Marker(
            placeId = place.id,
            name = place.name.toString(),
            latitude = place.latLng?.latitude ?: 0.0,
            longtitude = place.latLng?.longitude ?: 0.0,
            phone = place.phoneNumber.toString(),
            address = place.address.toString()
        )
    }

    val allMarkers = markerDao.getAll()

}
package com.huy.mappies.viewmodel

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import com.google.android.libraries.places.api.model.Place
import com.huy.mappies.repository.MarkerRepo
import timber.log.Timber
import javax.inject.Inject

class MapsViewModel @Inject constructor(
    private val markerRepo: MarkerRepo
) : ViewModel() {

    fun addMarkerFromPlace(place: Place, image: Bitmap?) {

        val marker = markerRepo.createMarker(place)
        val newId = markerRepo.addMarker(marker)

        Timber.i("New marker $newId added to db")

    }

}
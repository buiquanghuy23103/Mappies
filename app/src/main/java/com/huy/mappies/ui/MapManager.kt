package com.huy.mappies.ui

import android.graphics.Bitmap
import android.util.LongSparseArray
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.*
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.model.RectangularBounds
import com.google.android.libraries.places.api.net.FetchPlaceRequest
import com.google.android.libraries.places.api.net.PlacesClient
import com.huy.mappies.R
import com.huy.mappies.model.BookmarkView
import com.huy.mappies.model.PlaceInfo
import com.huy.mappies.utils.buildPhotoRequest
import com.huy.mappies.utils.categoryToIconMap
import com.huy.mappies.utils.placeFields
import timber.log.Timber

class MapManager(
    private val map: GoogleMap,
    private val activity: MapsActivity,
    infoWindowAdapter: GoogleMap.InfoWindowAdapter
) {


    private val fusedLocationClient: FusedLocationProviderClient
    private val placesClient: PlacesClient
    private val markers = LongSparseArray<Marker>()

    init {
        val apiKey = activity.getString(R.string.google_maps_key)
        Places.initialize(activity, apiKey)

        placesClient = Places.createClient(activity)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(activity)

        map.setInfoWindowAdapter(infoWindowAdapter)
        setupPoiClickListener()
    }

    private fun setupPoiClickListener() {
        map.setOnPoiClickListener {
            val placeRequest = buildPlaceRequest(it)
            placesClient.fetchPlace(placeRequest)
                .addOnSuccessListener { response ->
                    val place = response.place
                    displayPoiDetails(place)
                }
                .addOnFailureListener {error ->
                    if (error is ApiException) {
                        Timber.e("Place not found: ${error.message}, statusCode=${error.statusCode}")
                    }
                }
        }
    }

    private fun buildPlaceRequest(pointOfInterest: PointOfInterest)
            = FetchPlaceRequest.builder(pointOfInterest.placeId, placeFields)
        .build()

    private fun displayPoiDetails(place: Place) {

        val maxWidth = activity.resources.getDimensionPixelSize(R.dimen.default_image_width)
        val maxHeight = activity.resources.getDimensionPixelSize(R.dimen.default_image_height)
        val photoRequest = buildPhotoRequest(place, maxWidth, maxHeight)

        if (photoRequest == null) {
            addPlaceMarker(place, null)
            return
        } else {

            placesClient.fetchPhoto(photoRequest)
                .addOnSuccessListener { response ->
                    val bitmap = response.bitmap
                    addPlaceMarker(place, bitmap)
                }
                .addOnFailureListener {error ->
                    if (error is ApiException) {
                        Timber.e("Place photo not found: ${error.message}, statusCode=${error.statusCode}")
                    } else {
                        Timber.e(error)
                    }
                }

        }

    }

    private fun addPlaceMarker(place: Place, bitmap: Bitmap?) {

        val defaultMarkerIcon = BitmapDescriptorFactory.defaultMarker(
            BitmapDescriptorFactory.HUE_AZURE
        )

        val markerOptions = MarkerOptions()
            .position(place.latLng ?: LatLng(42.90237, -78.8704978))
            .title(place.name)
            .snippet(place.phoneNumber)
            .icon(defaultMarkerIcon)
            .alpha(0.08f)

        val marker = map.addMarker(markerOptions)
        marker.tag = PlaceInfo(place, bitmap)

        marker.showInfoWindow()
    }

    fun setupInfoWindowClickListener(handleInfoWindowClick: (Marker) -> Unit) {
        map.setOnInfoWindowClickListener {
            handleInfoWindowClick(it)
        }
    }

    fun displayBookmarkViews(bookmarkViews: List<BookmarkView>?) {
        markers.clear()
        map.clear()
        bookmarkViews?.let {
            it.forEach(this::displayBookmarkView)
        }
    }

    private fun displayBookmarkView(bookmarkView: BookmarkView) {
        val iconResId = categoryToIconMap[bookmarkView.category]
        val icon = iconResId?.let { BitmapDescriptorFactory.fromResource(iconResId) }

        val markerOptions = MarkerOptions()
            .position(LatLng(bookmarkView.latitude, bookmarkView.longtitude))
            .title(bookmarkView.name)
            .snippet(bookmarkView.phone)
            .icon(icon)
            .alpha(0.8f)

        val marker = map.addMarker(markerOptions)
        marker.tag = bookmarkView

        bookmarkView.id?.let {
            markers.append(it, marker)
        }

    }

    fun zoomBookmarkView(bookmarkView: BookmarkView) {

        val marker = bookmarkView.id?.let { markers[it] }
        marker?.showInfoWindow()

        zoomLocation(bookmarkView.latitude, bookmarkView.longtitude)
    }


    private fun zoomLocation(latitude: Double, longtitude: Double) {
        val latLng = LatLng(latitude, longtitude)
        val cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 16.0f)
        map.animateCamera(cameraUpdate)
    }

    fun getCurrentLocation(
        locationPermissionNotGranted: Boolean,
        requestLocationPermissions: () -> Unit
    ) {

        if (locationPermissionNotGranted) {

            requestLocationPermissions()

        } else {

            map.isMyLocationEnabled = true
            zoomInToCurrentLocation()

        }

    }

    private fun zoomInToCurrentLocation() {
        fusedLocationClient.lastLocation
            .addOnCompleteListener {

                val location = it.result

                if (location != null) {

                    val latLng = LatLng(location.latitude, location.longitude)
                    val markerOptions = MarkerOptions().position(latLng)
                        .title("You are here")

                    map.clear() // Remove previous markers
                    map.addMarker(markerOptions)
                    zoomLocation(location.latitude, location.longitude)

                } else {
                    Timber.e("No location found")
                }

            }
    }


    fun rectangularBounds(): RectangularBounds {
        return RectangularBounds.newInstance(map.projection.visibleRegion.latLngBounds)
    }

    fun displaySearchResults(place: Place) {
        place.latLng?.let { zoomLocation(it.latitude, it.longitude) }
        displayPoiDetails(place)
    }

}
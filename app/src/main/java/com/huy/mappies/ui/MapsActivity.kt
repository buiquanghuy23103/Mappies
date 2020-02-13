package com.huy.mappies.ui

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PointOfInterest
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.PhotoMetadata
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.FetchPhotoRequest
import com.google.android.libraries.places.api.net.FetchPlaceRequest
import com.google.android.libraries.places.api.net.PlacesClient
import com.huy.mappies.R
import com.huy.mappies.adapter.MarkerInfoWindowAdapter
import timber.log.Timber

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    companion object {
        private const val REQUEST_LOCATION = 1
        private const val LOCATION_PERMISSION = Manifest.permission.ACCESS_FINE_LOCATION
    }

    private lateinit var map: GoogleMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var placesClient: PlacesClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        setupLocationClient()
        setupPlacesClient()
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        setupInfoWindow()
        getCurrentLocation()
        setupPoiClickListener()
    }

    private fun setupLocationClient() {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
    }

    private fun setupPlacesClient() {
        val apiKey = getString(R.string.google_maps_key)
        Places.initialize(this, apiKey)
        placesClient = Places.createClient(this)
    }

    private fun getCurrentLocation() {

        if (locationPermissionNotGranted()) {

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
                    val marker = MarkerOptions().position(latLng)
                        .title("You are here")
                    val cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 16.0f)

                    map.clear() // Remove previous marker
                    map.addMarker(marker)
                    map.moveCamera(cameraUpdate)


                } else {
                    Timber.e("No location found")
                }

            }
    }

    private fun locationPermissionNotGranted(): Boolean {
        val permission = ActivityCompat.checkSelfPermission(this,
            LOCATION_PERMISSION
        )
        val grantedPermission = PackageManager.PERMISSION_GRANTED
        return permission != grantedPermission
    }

    private fun requestLocationPermissions() {
        val permissions = arrayOf(LOCATION_PERMISSION)
        ActivityCompat.requestPermissions(this, permissions,
            REQUEST_LOCATION
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == REQUEST_LOCATION) {
            if (grantResults.size == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getCurrentLocation()
            } else {
                Timber.e("Location permission denied")
            }
        }
    }

    private fun setupPoiClickListener() {
        map.setOnPoiClickListener {
            displayPlaceDetail(it)
        }
    }

    private fun displayPlaceDetail(pointOfInterest: PointOfInterest) {

        placesClient.fetchPlace(getPlaceDetailRequest(pointOfInterest))
            .addOnSuccessListener { response ->
                val place = response.place
                displayPlacePhoto(place)
            }
            .addOnFailureListener {error ->
                if (error is ApiException) {
                    Timber.e("Place not found: ${error.message}, statusCode=${error.statusCode}")
                }
            }

    }

    private fun getPlaceDetailRequest(pointOfInterest: PointOfInterest): FetchPlaceRequest {
        val placeId = pointOfInterest.placeId

        val placeFields = listOf(
            Place.Field.ID,
            Place.Field.NAME,
            Place.Field.PHONE_NUMBER,
            Place.Field.PHOTO_METADATAS,
            Place.Field.ADDRESS,
            Place.Field.LAT_LNG
        )

        return FetchPlaceRequest.builder(placeId, placeFields)
            .build()
    }

    private fun displayPlacePhoto(place: Place) {

        val photoMetadata: PhotoMetadata? = place.photoMetadatas?.get(0)

        if (photoMetadata == null) {
            addPhotoToMarker(place, null)
            return
        } else {

            val photoRequest = FetchPhotoRequest.builder(photoMetadata)
                .setMaxWidth(resources.getDimensionPixelSize(R.dimen.default_image_width))
                .setMaxHeight(resources.getDimensionPixelSize(R.dimen.default_image_height))
                .build()

            placesClient.fetchPhoto(photoRequest)
                .addOnSuccessListener { response ->
                    val bitmap = response.bitmap
                    addPhotoToMarker(place, bitmap)
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

    private fun addPhotoToMarker(place: Place, bitmap: Bitmap?) {

        val markerOptions = MarkerOptions()
            .position(place.latLng ?: LatLng(42.90237, -78.8704978))
            .title(place.name)
            .snippet(place.phoneNumber)

        map.clear()
        val marker = map.addMarker(markerOptions)
        marker.tag = bitmap
    }

    private fun setupInfoWindow() {
        val infoWindowAdapter = MarkerInfoWindowAdapter.from(layoutInflater)
        map.setInfoWindowAdapter(infoWindowAdapter)
    }

}

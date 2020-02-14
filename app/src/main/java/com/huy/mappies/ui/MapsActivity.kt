package com.huy.mappies.ui

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.PhotoMetadata
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.FetchPhotoRequest
import com.google.android.libraries.places.api.net.FetchPlaceRequest
import com.google.android.libraries.places.api.net.PlacesClient
import com.huy.mappies.R
import com.huy.mappies.adapter.BookmarkInfoWindowAdapter
import com.huy.mappies.model.BookmarkView
import com.huy.mappies.model.PlaceInfo
import com.huy.mappies.utils.getAppInjector
import com.huy.mappies.viewmodel.MapsViewModel
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    companion object {
        private const val REQUEST_LOCATION = 1
        private const val LOCATION_PERMISSION = Manifest.permission.ACCESS_FINE_LOCATION
    }

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private lateinit var map: GoogleMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var placesClient: PlacesClient
    private lateinit var viewModel: MapsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        getAppInjector().inject(this)
        setupViewModel()
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        setupLocationClient()
        setupPlacesClient()
    }

    private fun setupViewModel() {
        viewModel = ViewModelProviders.of(this, viewModelFactory)
            .get(MapsViewModel::class.java)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        setupMapsListener()
        observeBookmarkViews()
        getCurrentLocation()
    }

    private fun setupLocationClient() {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
    }

    private fun setupPlacesClient() {
        val apiKey = getString(R.string.google_maps_key)
        Places.initialize(this, apiKey)
        placesClient = Places.createClient(this)
    }

    private fun observeBookmarkViews() {

        viewModel.allBookmarkViews.observe(this, Observer {
            map.clear()
            it?.let {
                it.forEach(this::displayBookmark)
            }
        })

    }

    private fun displayBookmark(bookmarkView: BookmarkView) {
        val defaultMarkerIcon = BitmapDescriptorFactory.defaultMarker(
            BitmapDescriptorFactory.HUE_AZURE
        )

        val markerOptions = MarkerOptions()
            .position(LatLng(bookmarkView.latitude, bookmarkView.longtitude))
            .icon(defaultMarkerIcon)
            .alpha(0.8f)

        val marker = map.addMarker(markerOptions)
        marker.tag = bookmarkView
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
                    val markerOptions = MarkerOptions().position(latLng)
                        .title("You are here")
                    val cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 16.0f)

                    map.clear() // Remove previous marker
                    map.addMarker(markerOptions)
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

    private fun setupMapsListener() {
        setupInfoWindow()
        map.setOnPoiClickListener {
            handlePoiClick(it)
        }
        map.setOnInfoWindowClickListener {
            handleInfoWindowClick(it)
        }
    }

    private fun setupInfoWindow() {
        val infoWindowAdapter = BookmarkInfoWindowAdapter.from(layoutInflater)
        map.setInfoWindowAdapter(infoWindowAdapter)
    }

    private fun handlePoiClick(pointOfInterest: PointOfInterest) {

        placesClient.fetchPlace(getPlaceDetailRequest(pointOfInterest))
            .addOnSuccessListener { response ->
                val place = response.place
                displayPlaceDetails(place)
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

    private fun displayPlaceDetails(place: Place) {

        val photoRequest = getPhotoRequest(place)

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

    private fun getPhotoRequest(place: Place): FetchPhotoRequest? {
        val photoMetadata: PhotoMetadata? = place.photoMetadatas?.get(0)
        return if (photoMetadata == null) {
            null
        } else {
            FetchPhotoRequest.builder(photoMetadata)
                .setMaxWidth(resources.getDimensionPixelSize(R.dimen.default_image_width))
                .setMaxHeight(resources.getDimensionPixelSize(R.dimen.default_image_height))
                .build()
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

        map.clear()

        val marker = map.addMarker(markerOptions)
        marker.tag = PlaceInfo(place, bitmap)


        marker.showInfoWindow()
    }

    private fun handleInfoWindowClick(marker: Marker) {
        when(marker.tag) {

            is PlaceInfo -> {
                val placeInfo = marker.tag as PlaceInfo
                if (placeInfo.place != null) {
                    GlobalScope.launch {
                        viewModel.addBookmarkFromPlace(placeInfo.place, placeInfo.image)
                    }
                }
            }

            is BookmarkView -> {
                val bookmarkView = marker.tag as BookmarkView
                marker.hideInfoWindow()
                bookmarkView.id?.let {
                    startBookmarkDetailsActivity(it)
                }
            }
        }
        marker.remove()
    }

    private fun startBookmarkDetailsActivity(bookmarkId: Long) {
        val intent = Intent(this, BookmarkDetailsActivity::class.java)
            .putExtra(getString(R.string.intent_extra_bookmark_id), bookmarkId)
        startActivity(intent)
    }


}

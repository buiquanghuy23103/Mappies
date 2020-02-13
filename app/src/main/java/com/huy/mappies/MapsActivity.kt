package com.huy.mappies

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import timber.log.Timber

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    companion object {
        private const val REQUEST_LOCATION = 1
        private const val LOCATION_PERMISSION = Manifest.permission.ACCESS_FINE_LOCATION
    }

    private lateinit var map: GoogleMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        setupLocationClient()
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        getCurrentLocation()
        setupPoiClickListener()
    }

    private fun setupLocationClient() {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
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
        val permission = ActivityCompat.checkSelfPermission(this, LOCATION_PERMISSION)
        val grantedPermission = PackageManager.PERMISSION_GRANTED
        return permission != grantedPermission
    }

    private fun requestLocationPermissions() {
        val permissions = arrayOf(LOCATION_PERMISSION)
        ActivityCompat.requestPermissions(this, permissions, REQUEST_LOCATION)
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
            Toast.makeText(this, it.name, Toast.LENGTH_LONG)
                .show()
        }
    }

}

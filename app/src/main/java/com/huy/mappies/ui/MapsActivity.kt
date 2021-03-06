package com.huy.mappies.ui

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.google.android.gms.common.GooglePlayServicesNotAvailableException
import com.google.android.gms.common.GooglePlayServicesRepairableException
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.Marker
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import com.huy.mappies.R
import com.huy.mappies.adapter.BookmarkInfoWindowAdapter
import com.huy.mappies.adapter.DrawerItemListAdapter
import com.huy.mappies.model.BookmarkView
import com.huy.mappies.model.PlaceInfo
import com.huy.mappies.utils.getAppInjector
import com.huy.mappies.utils.placeFields
import com.huy.mappies.viewmodel.MapsViewModel
import kotlinx.android.synthetic.main.activity_maps.*
import kotlinx.android.synthetic.main.maps_drawer_view.*
import kotlinx.android.synthetic.main.maps_main_view.*
import timber.log.Timber
import javax.inject.Inject

class MapsActivity : AppCompatActivity(), OnMapReadyCallback, DrawerItemListAdapter.OnDrawerItemClick {

    companion object {
        private const val REQUEST_LOCATION = 1
        private const val REQUEST_AUTOCOMPLETE = 2
        private const val LOCATION_PERMISSION = Manifest.permission.ACCESS_FINE_LOCATION
    }

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private lateinit var placesClient: PlacesClient
    private lateinit var viewModel: MapsViewModel
    private lateinit var mapManager: MapManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        getAppInjector().inject(this)
        setupViewModel()
        setupToolbar()
        setupDrawerToggleIcon()
        setupMapView()
        setupPlacesClient()
        setupDrawer()
        setupSearchButton()
    }

    override fun onMapReady(googleMap: GoogleMap) {
        val infoWindowAdapter = BookmarkInfoWindowAdapter.from(layoutInflater)
        mapManager = MapManager(googleMap, this, infoWindowAdapter)
        mapManager.setupInfoWindowClickListener {
            handleInfoWindowClick(it)
        }
        observeBookmarkViews()
    }

    private fun setupViewModel() {
        viewModel = ViewModelProviders.of(this, viewModelFactory)
            .get(MapsViewModel::class.java)
    }

    private fun setupToolbar() {
        setSupportActionBar(maps_toolbar)
    }

    private fun setupDrawerToggleIcon() {
        val toggle = ActionBarDrawerToggle(
            this,
            maps_drawer_layout,
            maps_toolbar,
            R.string.open_drawer,
            R.string.close_drawer
        )

        toggle.syncState()
    }

    private fun setupDrawer() {
        val adapter = DrawerItemListAdapter(this)
        drawerRecyclerView.adapter = adapter
        viewModel.allBookmarkViews.observe(this, Observer {
            adapter.submitList(it)
        })
    }

    private fun setupSearchButton() {
        search_button.setOnClickListener { searchAtCurrentLocation() }
    }

    private fun setupMapView() {
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    private fun setupPlacesClient() {
        val apiKey = getString(R.string.google_maps_key)
        Places.initialize(this, apiKey)
        placesClient = Places.createClient(this)
    }

    private fun observeBookmarkViews() {

        viewModel.allBookmarkViews.observe(this, Observer {
            mapManager.displayBookmarkViews(it)
        })

    }

    override fun handleDrawerItemClick(bookmarkView: BookmarkView) {

        maps_drawer_layout.closeDrawer(drawerView)

        mapManager.zoomBookmarkView(bookmarkView)

    }


    private fun getCurrentLocation() {

        mapManager.getCurrentLocation(locationPermissionNotGranted()){
            requestLocationPermissions()
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

    private fun handleInfoWindowClick(marker: Marker) {
        when(marker.tag) {

            is PlaceInfo -> {
                val placeInfo = marker.tag as PlaceInfo
                if (placeInfo.place != null) {
                    viewModel.savePlaceInfoToDb(placeInfo.place, placeInfo.image)
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

    private fun searchAtCurrentLocation() {

        try {
            val intent = Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY, placeFields)
                .setLocationBias(mapManager.rectangularBounds())
                .build(this)
            startActivityForResult(intent, REQUEST_AUTOCOMPLETE)
        } catch (error: GooglePlayServicesRepairableException) {
            Timber.e(error)
            // TODO: Handle exception
        } catch (error: GooglePlayServicesNotAvailableException) {
            Timber.e(error)
            // TODO: Handle exception
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when(requestCode) {
            REQUEST_AUTOCOMPLETE -> {
                if (resultCode == Activity.RESULT_OK && data != null) {
                    val place = Autocomplete.getPlaceFromIntent(data)
                    mapManager.displaySearchResults(place)
                }
            }
        }
    }

}

package com.huy.mappies.ui

import android.util.LongSparseArray
import com.huawei.hms.common.ApiException
import com.huawei.hms.location.FusedLocationProviderClient
import com.huawei.hms.location.LocationServices
import com.huawei.hms.maps.CameraUpdateFactory
import com.huawei.hms.maps.HuaweiMap
import com.huawei.hms.maps.model.BitmapDescriptorFactory
import com.huawei.hms.maps.model.LatLng
import com.huawei.hms.maps.model.Marker
import com.huawei.hms.maps.model.MarkerOptions
import com.huawei.hms.site.api.SearchResultListener
import com.huawei.hms.site.api.SearchService
import com.huawei.hms.site.api.SearchServiceFactory
import com.huawei.hms.site.api.model.DetailSearchResponse
import com.huawei.hms.site.api.model.SearchStatus
import com.huawei.hms.site.api.model.Site
import com.huawei.hms.site.widget.SearchIntent
import com.huy.mappies.R
import com.huy.mappies.model.BookmarkView
import com.huy.mappies.model.SiteInfo
import com.huy.mappies.utils.buildSiteRequest
import com.huy.mappies.utils.categoryToIconMap
import timber.log.Timber

class HuaweiMapManager(
    private val map: HuaweiMap,
    private val activity: MapsActivity,
    infoWindowAdapter: HuaweiMap.InfoWindowAdapter
) {


    private val fusedLocationClient: FusedLocationProviderClient
    private val placesClient: SearchService
    private val markers = LongSparseArray<Marker>()
    private val apiKey = activity.getString(R.string.huawei_maps_key)

    init {

        placesClient = SearchServiceFactory.create(activity, apiKey)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(activity)

        map.setInfoWindowAdapter(infoWindowAdapter)
        setupPoiClickListener()
    }

    private fun setupPoiClickListener() {
        map.setOnPoiClickListener {poi ->
            val placeRequest = buildSiteRequest(poi)
            placesClient.detailSearch(placeRequest, object: SearchResultListener<DetailSearchResponse> {
                override fun onSearchResult(response: DetailSearchResponse?) {
                    response?.site?.let {site ->
                        addPlaceMarker(site)
                    } ?: throw Exception("Invalid place Id")

                }

                override fun onSearchError(errorStatus: SearchStatus?) {
                    if (errorStatus is ApiException) {
                        Timber.e("Place not found: ${errorStatus.message}, statusCode=${errorStatus.statusCode}")
                    }
                }
            })
        }
    }

    private fun addPlaceMarker(site: Site) {

        val defaultMarkerIcon = BitmapDescriptorFactory.defaultMarker(
            BitmapDescriptorFactory.HUE_AZURE
        )

        val coordinate = site.location

        val markerOptions = MarkerOptions()
            .position(LatLng(coordinate.lat, coordinate.lng))
            .title(site.name)
            .snippet(site.address.adminArea)
            .icon(defaultMarkerIcon)
            .alpha(0.08f)

        val marker = map.addMarker(markerOptions)
        marker.tag = SiteInfo(site, null)

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

    fun zoomInToCurrentLocation() {
        map.isMyLocationEnabled = true

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
//
//
//    fun rectangularBounds(): RectangularBounds {
//        return RectangularBounds.newInstance(map.projection.visibleRegion.latLngBounds)
//    }
//
    fun displaySearchResults(site: Site) {
        site.location?.let {
            zoomLocation(it.lat, it.lng)
        }
        addPlaceMarker(site)
    }

    fun getSearchIntent(): SearchIntent {
        return SearchIntent().apply {
            setApiKey(apiKey)
        }
    }

}
package com.huy.mappies.adapter

import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.Marker
import com.huy.mappies.R
import com.huy.mappies.model.PlaceInfo

class MarkerInfoWindowAdapter private constructor(private val markerView: View)
    : GoogleMap.InfoWindowAdapter
{

    companion object {
        fun from(inflater: LayoutInflater): MarkerInfoWindowAdapter {
            val view = inflater.inflate(R.layout.marker_layout, null)
            return MarkerInfoWindowAdapter(view)
        }
    }

    override fun getInfoWindow(p0: Marker?): View? {
        // If not replacing the entire window, return null
        return null
    }

    override fun getInfoContents(marker: Marker?): View {

        val placeInfo: PlaceInfo? = marker?.tag as PlaceInfo

        val titleTextView = markerView.findViewById<TextView>(R.id.marker_title)
        titleTextView.text = marker.title.toString()

        val subtitleTextView = markerView.findViewById<TextView>(R.id.marker_subtitle)
        subtitleTextView.text = marker.snippet ?: ""

        val imageView = markerView.findViewById<ImageView>(R.id.marker_image)
        imageView.setImageBitmap(placeInfo?.image)

        return markerView
    }
}
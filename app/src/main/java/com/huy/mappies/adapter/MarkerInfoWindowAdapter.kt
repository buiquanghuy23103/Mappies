package com.huy.mappies.adapter

import android.view.LayoutInflater
import android.view.View
import androidx.databinding.DataBindingUtil
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.Marker
import com.huy.mappies.R
import com.huy.mappies.databinding.BookmarkLayoutBinding
import com.huy.mappies.model.BookmarkView
import com.huy.mappies.model.PlaceInfo

class MarkerInfoWindowAdapter private constructor(private val binding: BookmarkLayoutBinding)
    : GoogleMap.InfoWindowAdapter
{

    companion object {
        fun from(inflater: LayoutInflater): MarkerInfoWindowAdapter {
            val binding: BookmarkLayoutBinding = DataBindingUtil.inflate(
                inflater,
                R.layout.bookmark_layout,
                null,
                false
            )
            return MarkerInfoWindowAdapter(binding)
        }
    }

    override fun getInfoWindow(p0: Marker?): View? {
        // If not replacing the entire window, return null
        return null
    }

    override fun getInfoContents(marker: Marker?): View {

        with(binding) {
            bookmarkTitle.text = marker?.title
            bookmarkSubtitle?.text = marker?.snippet
        }

        displayImage(marker)

        return binding.root

    }

    private fun displayImage(marker: Marker?) {
        when(marker?.tag) {

            is PlaceInfo -> {
                val placeInfo: PlaceInfo? = marker.tag as PlaceInfo
                binding.bookmarkImage.setImageBitmap(placeInfo?.image)
            }

            is BookmarkView -> {
                val bookmarkView = marker.tag as BookmarkView
                val bitmap = bookmarkView.getImage(binding.root.context)
                binding.bookmarkImage.setImageBitmap(bitmap)
            }

        }
    }

}
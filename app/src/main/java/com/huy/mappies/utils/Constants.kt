package com.huy.mappies.utils

import com.google.android.libraries.places.api.model.Place

const val RESTAURANT = "Restaurant"
const val GAS = "Gas"
const val SHOPPING = "Shopping"
const val LODGING = "Lodging"
const val OTHER = "Other"

val placeFields = listOf(
    Place.Field.ID,
    Place.Field.NAME,
    Place.Field.PHONE_NUMBER,
    Place.Field.PHOTO_METADATAS,
    Place.Field.ADDRESS,
    Place.Field.LAT_LNG,
    Place.Field.TYPES
)

package com.huy.mappies.utils

import com.google.android.libraries.places.api.model.Place
import com.huy.mappies.MainApplication
import com.huy.mappies.R

fun getAppInjector() = MainApplication.get().component

fun buildPlaceTypeToCategoryMap() = hashMapOf(
    Place.Type.BAKERY to RESTAURANT,
    Place.Type.BAR to RESTAURANT,
    Place.Type.CAFE to RESTAURANT,
    Place.Type.RESTAURANT to RESTAURANT,
    Place.Type.MEAL_DELIVERY to RESTAURANT,
    Place.Type.MEAL_TAKEAWAY to RESTAURANT,

    Place.Type.GAS_STATION to GAS,
    Place.Type.CLOTHING_STORE to SHOPPING,
    Place.Type.DEPARTMENT_STORE to SHOPPING,
    Place.Type.FURNITURE_STORE to SHOPPING,
    Place.Type.GROCERY_OR_SUPERMARKET to SHOPPING,
    Place.Type.HARDWARE_STORE to SHOPPING,
    Place.Type.HOME_GOODS_STORE to SHOPPING,
    Place.Type.JEWELRY_STORE to SHOPPING,
    Place.Type.SHOE_STORE to SHOPPING,
    Place.Type.SHOPPING_MALL to SHOPPING,
    Place.Type.STORE to SHOPPING,

    Place.Type.LODGING to LODGING
)

fun buildCategoryToIconMap() = hashMapOf(
    RESTAURANT to R.drawable.ic_restaurant,
    GAS to R.drawable.ic_gas,
    SHOPPING to R.drawable.ic_shopping,
    LODGING to R.drawable.ic_lodging,
    OTHER to R.drawable.ic_other
)
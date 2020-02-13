package com.huy.mappies.viewmodel

import androidx.lifecycle.ViewModel
import com.huy.mappies.repository.MarkerRepo
import javax.inject.Inject

class MapsViewModel @Inject constructor(
    private val markerRepo: MarkerRepo
) : ViewModel() {



}
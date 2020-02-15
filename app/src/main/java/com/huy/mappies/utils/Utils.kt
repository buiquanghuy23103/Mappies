package com.huy.mappies.utils

import com.huy.mappies.MainApplication

const val authority = "com.huy.mappies.fileprovider"

fun getAppInjector() = MainApplication.get().component
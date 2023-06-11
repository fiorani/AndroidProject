package com.example.eatit.model

import android.location.Location

data class Restaurant(
    var name: String = "",
    var address: String = "",
    var photo: String = "",
    var numRatings: Int = 0,
    var avgRating: Float = 0.0f,
    var id: String? = "",
    var userId: String = "",
    var phone: String = "",
    var distance: Int? = null,
)

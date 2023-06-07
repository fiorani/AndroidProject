package com.example.eatit.model

data class Restaurant(
    var name: String? = null,
    var address: String? = null,
    var photo: String? = null,
    var numRatings: Int = 0,
    var avgRating: Float = 0.0f,
    var id: String? = null,
    var userId: String? = null,
    var phone: String? = null,
)

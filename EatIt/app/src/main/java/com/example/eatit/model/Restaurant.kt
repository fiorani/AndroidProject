package com.example.eatit.model

data class Restaurant(
    var name: String? = null,
    var city: String? = null,
    var category: String? = null,
    var photo: String? = null,
    var price: Int = 0,
    var numRatings: Int = 0,
    var avgRating: Double = 0.toDouble(),
    var id: String? = null,
)

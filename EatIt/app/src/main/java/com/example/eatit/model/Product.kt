package com.example.eatit.model

data class Product(
    var name: String? = null,
    var description: String? = null,
    var price: Float? = 0.0f,
    var photo: String? = null,
    var id: String? = null,
    var section: String? = null
)
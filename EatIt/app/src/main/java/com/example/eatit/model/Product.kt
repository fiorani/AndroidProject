package com.example.eatit.model

import com.google.firebase.firestore.DocumentId

data class Product(
    var name: String? = null,
    var description: String? = null,
    var price: String? = null,
    var photo: String? = null,
    var id: String? = null
)
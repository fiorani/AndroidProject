package com.example.eatit.model

import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ServerTimestamp
import com.google.firebase.ktx.Firebase
import java.util.Date

data class Orders(
    var userId: String? = null,
    var restaurantId: String? = null,
    var userProducts: List<Product>? = null,
    var totalPrice: Double = 0.toDouble(),
    @ServerTimestamp var timestamp: Date? = null,
) {
    constructor(products: List<Product>, price: Double, restaurantId: String?) : this() {
        val user = Firebase.auth.currentUser
        this.userId = user?.uid
        this.restaurantId = restaurantId
        this.userProducts = products
        this.totalPrice = price
    }
}

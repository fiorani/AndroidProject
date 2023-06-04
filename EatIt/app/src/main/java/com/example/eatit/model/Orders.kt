package com.example.eatit.model

import androidx.compose.runtime.Immutable
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.IgnoreExtraProperties
import com.google.firebase.firestore.ServerTimestamp
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.MutableStateFlow
import java.util.Date

data class Orders(
    var userId: String? = null,
    var restaurantId: String? = null,
    var listProductId: ArrayList<String>? = null,
    var listQuantity: ArrayList<String>? = null,
    var listPrice: ArrayList<String>? = null,
    var totalPrice: Double = 0.toDouble(),
    @ServerTimestamp var timestamp: Date? = null,
){
    constructor(productId: ArrayList<String>,quantity: ArrayList<String>,prices: ArrayList<String>, price: Double, restaurantId: String?) : this() {
        val user = Firebase.auth.currentUser
        this.userId = user?.uid
        this.restaurantId = restaurantId
        this.listProductId = productId
        this.listQuantity = quantity
        this.totalPrice = price
        this.listPrice = prices
    }
}

package com.example.eatiitwear.presentation

import com.google.firebase.firestore.ServerTimestamp
import java.util.Date

data class Order(
    var userId: String = "",
    var restaurantId: String = "",
    var listProductId: ArrayList<String> = ArrayList(mutableListOf()),
    var listQuantity: ArrayList<Int> = ArrayList(mutableListOf()),
    var listPrice: ArrayList<Float> = ArrayList(mutableListOf()),
    var totalPrice: Float = 0.0f,
    var id: String? = "",
    var status: String = "attesa pagamento",
    @ServerTimestamp var timestamp: Date = Date(),
)

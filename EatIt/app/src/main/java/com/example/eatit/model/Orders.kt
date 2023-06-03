package com.example.eatit.model

import android.text.TextUtils
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.ServerTimestamp
import java.util.Date

data class Orders (
    var userId: String? = null,
    var userName: String? = null,
    var userProducts: List<Product>? = null,
    var totalPrice: Double = 0.toDouble(),
    @ServerTimestamp var timestamp: Date? = null,
) {

    constructor(user: FirebaseUser,products: List<Product>, price: Double) : this() {
        this.userId = user.uid
        this.userName = user.displayName
        if (TextUtils.isEmpty(this.userName)) {
            this.userName = user.email
        }
        this.userProducts = products
        this.totalPrice = price
    }
}

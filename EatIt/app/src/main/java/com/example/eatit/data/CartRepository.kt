package com.example.eatit.data

import androidx.annotation.WorkerThread
import com.example.eatit.EatItApp
import com.example.eatit.model.Orders
import com.example.eatit.model.User
import com.google.firebase.firestore.FirebaseFirestore

class CartRepository(eatItApp: EatItApp) {
    @WorkerThread
    suspend fun insertNewOrder(order: Orders) {
        FirebaseFirestore.getInstance().collection("orders").add(order)
    }
}
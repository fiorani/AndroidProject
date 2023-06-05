package com.example.eatit.data

import androidx.annotation.WorkerThread
import com.example.eatit.EatItApp
import com.example.eatit.model.Order
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase

class CartRepository(eatItApp: EatItApp) {
    @WorkerThread
    suspend fun insertNewOrder(order: Order) {
        FirebaseFirestore.getInstance().collection("orders").add(order)
    }


    fun getOrders(): List<DocumentSnapshot> {
        val orders = mutableListOf<DocumentSnapshot>()
        FirebaseFirestore.getInstance().collection("orders").get()
            .addOnSuccessListener { querySnapshot ->
                for (document in querySnapshot) {
                    if (document.data["userId"].toString()
                            .contains(Firebase.auth.currentUser?.uid.toString(), ignoreCase = true)
                    ) {
                        orders.add(document)
                    }
                }
            }.addOnFailureListener { exception ->
                println("Error getting restaurants: $exception")
            }
        return orders
    }

    fun getProducts(order: DocumentSnapshot): List<DocumentSnapshot> {
        val products = mutableListOf<DocumentSnapshot>()
        FirebaseFirestore.getInstance().collection("restaurants")
            .document(order.data?.get("restaurantId").toString())
            .collection("products").get().addOnSuccessListener { querySnapshot ->
                for (document in querySnapshot) {
                    for (product in order.data?.get("listProductId") as ArrayList<String>) {
                        if (product.contains(document.id)) {
                            products.add(document)
                        }
                    }
                }
            }.addOnFailureListener { exception ->
                println("Error getting restaurants: $exception")
            }
        return products
    }
}
package com.example.eatit.data

import android.util.Log
import androidx.annotation.WorkerThread
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshots.SnapshotStateList
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

    @Composable
    fun getOrders(): SnapshotStateList<DocumentSnapshot> {
        val orders = remember { mutableStateListOf<DocumentSnapshot>() }
        FirebaseFirestore.getInstance().collection("orders").get()
            .addOnSuccessListener { querySnapshot ->
                for (document in querySnapshot) {
                    if (document.data.get("userId").toString()
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

    @Composable
    fun getProducts(order: DocumentSnapshot): SnapshotStateList<DocumentSnapshot> {
        var products = remember { mutableStateListOf<DocumentSnapshot>() }
        FirebaseFirestore.getInstance().collection("restaurants").document(order.data?.get("restaurantId").toString())
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
package com.example.eatiitwear.presentation

import com.example.eatit.model.Product
import com.example.eatit.model.Restaurant
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class Repo {
    suspend fun getOrders(): List<Order> = withContext(Dispatchers.IO) {
        try {
            FirebaseFirestore.getInstance().collection("orders")
                .whereEqualTo("userId", Firebase.auth.currentUser?.uid)
                .orderBy("timestamp", Query.Direction.DESCENDING).get().await()
                .documents.mapNotNull { documentSnapshot ->
                    val order = documentSnapshot.toObject(Order::class.java)
                    order?.id = documentSnapshot.id
                    order
                }
        } catch (e: Exception) {
            throw e
        }
    }

    suspend fun getProducts(order: Order): List<Product> = withContext(Dispatchers.IO) {
        try {
            val products = mutableListOf<Product>()
            val querySnapshot = FirebaseFirestore.getInstance().collection("restaurants")
                .document(order.restaurantId)
                .collection("products").get().await()
                .documents
            for (documentSnapshot in querySnapshot) {
                if (order.listProductId.contains(documentSnapshot.id)) {
                    val product = documentSnapshot.toObject(Product::class.java)
                    product?.id = documentSnapshot.id
                    product?.let { products.add(it) }
                }
            }
            products
        } catch (e: Exception) {
            throw e
        }
    }

    suspend fun getRestaurant(restaurantId: String): Restaurant =
        withContext(Dispatchers.IO) {
            try {
                val restaurant =
                    FirebaseFirestore.getInstance().collection("restaurants").document(restaurantId)
                        .get().await().toObject(Restaurant::class.java)!!
                restaurant.id = restaurantId
                restaurant
            } catch (e: Exception) {
                throw e
            }
        }
}
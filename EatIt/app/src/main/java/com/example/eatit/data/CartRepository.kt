package com.example.eatit.data

import androidx.annotation.WorkerThread
import com.example.eatit.EatItApp
import com.example.eatit.model.Order
import com.example.eatit.model.Product
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class CartRepository(eatItApp: EatItApp) {
    @WorkerThread
    suspend fun insertNewOrder(order: Order) {
        FirebaseFirestore.getInstance().collection("orders").add(order)
    }

    suspend fun getOrders(): List<Order> = withContext(Dispatchers.IO) {
        try {
            FirebaseFirestore.getInstance().collection("orders")
                .whereEqualTo("userId", Firebase.auth.currentUser?.uid)
                .orderBy("timestamp",Query.Direction.DESCENDING).get().await()
                .documents.mapNotNull { documentSnapshot ->
                    val order = documentSnapshot.toObject(Order::class.java)
                    order?.id = documentSnapshot.id
                    order
                }
        } catch (e: Exception) {
            throw e
        }
    }
    suspend fun getOrdersRestaurateur(): List<Order> = withContext(Dispatchers.IO) {

        try {
            val restaurants:List<String> = FirebaseFirestore.getInstance().collection("restaurants")
                .whereEqualTo("userId", Firebase.auth.currentUser?.uid).get().await().documents
                .mapNotNull { documentSnapshot ->
                    documentSnapshot.id
                }
            if(restaurants.isNotEmpty()){
                FirebaseFirestore.getInstance().collection("orders")
                    .whereIn("restaurantId", restaurants)
                    .orderBy("timestamp",Query.Direction.DESCENDING).get().await()
                    .documents.mapNotNull { documentSnapshot ->
                        val order = documentSnapshot.toObject(Order::class.java)
                        order?.id = documentSnapshot.id
                        order
                    }
            }
            emptyList<Order>()
        } catch (e: Exception) {
            throw e
        }
    }
    suspend fun getProducts(order: Order): List<Product> = withContext(Dispatchers.IO) {
        try {
            val products = mutableListOf<Product>()
            val querySnapshot = FirebaseFirestore.getInstance().collection("restaurants")
                .document(order.restaurantId.toString())
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
}
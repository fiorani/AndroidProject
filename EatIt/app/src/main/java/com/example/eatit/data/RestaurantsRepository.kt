package com.example.eatit.data

import androidx.annotation.WorkerThread
import com.example.eatit.EatItApp
import com.example.eatit.model.Product
import com.example.eatit.model.Restaurant
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore


class RestaurantsRepository(eatItApp: EatItApp) {

    @WorkerThread
    suspend fun insertNewRestaurant(restaurant: Restaurant) {
        FirebaseFirestore.getInstance().collection("restaurants").add(restaurant)
    }

    @WorkerThread
    suspend fun insertNewProduct(restaurantId: String, product: Product) {
        FirebaseFirestore.getInstance().collection("restaurants").document(restaurantId)
            .collection("products").add(product)
    }

    fun getRestaurants(): List<DocumentSnapshot> {
        val products = mutableListOf<DocumentSnapshot>()
        FirebaseFirestore.getInstance().collection("restaurants").get()
            .addOnSuccessListener { querySnapshot ->
                products.addAll(querySnapshot.documents)
            }.addOnFailureListener { exception ->
                println("Error getting restaurants: $exception")
            }
        return products
    }

    fun getProducts(restaurantId: String): List<DocumentSnapshot> {
        val products = mutableListOf<DocumentSnapshot>()
        FirebaseFirestore.getInstance().collection("restaurants").document(restaurantId)
            .collection("products").get().addOnSuccessListener { querySnapshot ->
                products.addAll(querySnapshot.documents)
            }.addOnFailureListener { exception ->
                println("Error getting restaurants: $exception")
            }
        return products
    }

    fun getRatings(restaurantId: String): List<DocumentSnapshot> {
        val products = mutableListOf<DocumentSnapshot>()
        FirebaseFirestore.getInstance().collection("restaurants").document(restaurantId)
            .collection("ratings").get().addOnSuccessListener { querySnapshot ->
                products.addAll(querySnapshot.documents)
            }.addOnFailureListener { exception ->
                println("Error getting restaurants: $exception")
            }
        return products
    }

    fun getRestaurant(restaurantId: String) =
        FirebaseFirestore.getInstance().collection("restaurants").document(restaurantId).get()

    fun getProduct(restaurantId: String, productId: String) =
        FirebaseFirestore.getInstance().collection("restaurants").document(restaurantId)
            .collection("products").document(productId).get()
}
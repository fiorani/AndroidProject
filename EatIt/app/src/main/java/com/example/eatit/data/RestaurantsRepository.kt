package com.example.eatit.data

import androidx.annotation.WorkerThread
import com.example.eatit.EatItApp
import com.example.eatit.model.Product
import com.example.eatit.model.Restaurant
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext


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

    suspend fun getRestaurants(): List<DocumentSnapshot> = withContext(Dispatchers.IO) {
        return@withContext try {
            val querySnapshot = FirebaseFirestore.getInstance().collection("restaurants").get().await()
            querySnapshot.documents
        } catch (exception: Exception) {
            println("Error getting restaurants: $exception")
            emptyList()
        }
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

    suspend fun getRestaurant(restaurantId: String): MutableList<DocumentSnapshot> = withContext(Dispatchers.IO) {
        try {
            val querySnapshot = FirebaseFirestore.getInstance().collection("restaurants").document(restaurantId).get().await()
            mutableListOf(querySnapshot)
        } catch (exception: Exception) {
            println("Error getting restaurant: $exception")
            mutableListOf()
        }
    }


}
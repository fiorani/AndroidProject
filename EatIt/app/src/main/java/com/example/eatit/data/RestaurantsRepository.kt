package com.example.eatit.data

import androidx.annotation.WorkerThread
import com.example.eatit.EatItApp
import com.example.eatit.model.Product
import com.example.eatit.model.Restaurant
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

    fun getRestaurants() = FirebaseFirestore.getInstance().collection("restaurants").get()

    fun getProducts(restaurantId: String) =
        FirebaseFirestore.getInstance().collection("restaurants").document(restaurantId)
            .collection("products").get()

    fun getRatings(restaurantId: String) =
        FirebaseFirestore.getInstance().collection("restaurants").document(restaurantId)
            .collection("ratings").get()

    fun getRestaurant(restaurantId: String) =
        FirebaseFirestore.getInstance().collection("restaurants").document(restaurantId).get()
}
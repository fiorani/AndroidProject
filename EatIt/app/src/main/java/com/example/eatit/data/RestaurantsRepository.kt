package com.example.eatit.data

import androidx.annotation.WorkerThread
import com.example.eatit.EatItApp
import com.example.eatit.model.Product
import com.example.eatit.model.Rating
import com.example.eatit.model.Restaurant
import com.example.eatit.model.User
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext


class RestaurantsRepository(eatItApp: EatItApp) {

    @WorkerThread
    suspend fun insertNewRestaurant(restaurant: Restaurant) {
        FirebaseFirestore.getInstance().collection("restaurants").add(restaurant)
    }

    @WorkerThread
    suspend fun insertNewProduct(restaurantId: DocumentId, product: Product) {
        FirebaseFirestore.getInstance().collection("restaurants").document(restaurantId.toString())
            .collection("products").add(product)
    }

    suspend fun getRestaurants(): List<Restaurant> = withContext(Dispatchers.IO) {
        try {
            FirebaseFirestore.getInstance().collection("restaurants").get().await()
            .documents.mapNotNull { it.toObject(Restaurant::class.java) }
        } catch (e: Exception) {
            throw e
        }
    }

    suspend fun getRestaurantsByUserId(userId: String): List<Restaurant> = withContext(Dispatchers.IO) {
        try {
            FirebaseFirestore.getInstance().collection("restaurants").whereEqualTo("userId", userId).get().await()
                .documents.mapNotNull { it.toObject(Restaurant::class.java) }
        } catch (e: Exception) {
            throw e
        }
    }


    suspend fun getProducts(restaurantId: String): List<Product> = withContext(Dispatchers.IO) {
        try{
            FirebaseFirestore.getInstance().collection("restaurants").document(restaurantId)
                .collection("products").get().await()
                .documents.mapNotNull { it.toObject(Product::class.java) }
        } catch (e: Exception) {
            throw e
        }
    }

    suspend fun getRatings(restaurantId: String): List<Rating> = withContext(Dispatchers.IO) {
    try{
        FirebaseFirestore.getInstance().collection("restaurants").document(restaurantId)
            .collection("ratings").get().await()
            .documents.mapNotNull { it.toObject(Rating::class.java) }
    } catch (e: Exception) {
        throw e
    }
    }


    suspend fun getRestaurant(restaurantId: String): Restaurant=
        withContext(Dispatchers.IO) {
            try {
                    FirebaseFirestore.getInstance().collection("restaurants").document(restaurantId)
                        .get().await().toObject(Restaurant::class.java)!!
            } catch (e: Exception) {
                throw e
            }
        }


}
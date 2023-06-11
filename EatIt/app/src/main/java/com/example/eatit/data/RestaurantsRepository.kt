package com.example.eatit.data

import android.net.Uri
import androidx.annotation.WorkerThread
import com.example.eatit.EatItApp
import com.example.eatit.model.Product
import com.example.eatit.model.Rating
import com.example.eatit.model.Restaurant
import com.example.eatit.model.User
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext


class RestaurantsRepository(eatItApp: EatItApp) {

    @WorkerThread
    suspend fun insertNewRestaurant(restaurant: Restaurant) {
        FirebaseFirestore.getInstance().collection("restaurants").add(restaurant)
    }

    @WorkerThread
    suspend fun insertNewProduct(restaurantId: String?, product: Product) {
        FirebaseFirestore.getInstance().collection("restaurants").document(restaurantId.toString())
            .collection("products").add(product)
    }

    suspend fun getRestaurants(): List<Restaurant> = withContext(Dispatchers.IO) {
        try {
            FirebaseFirestore.getInstance().collection("restaurants").get().await()
                .documents.mapNotNull { documentSnapshot ->
                    val restaurant = documentSnapshot.toObject(Restaurant::class.java)
                    restaurant?.id = documentSnapshot.id
                    restaurant
                }
        } catch (e: Exception) {
            throw e
        }
    }

    suspend fun getRestaurantsByUserId(): List<Restaurant> =
        withContext(Dispatchers.IO) {
            try {
                FirebaseFirestore.getInstance().collection("restaurants")
                    .whereEqualTo("userId",  Firebase.auth.currentUser?.uid.toString()).get().await()
                    .documents.mapNotNull { documentSnapshot ->
                        val restaurant = documentSnapshot.toObject(Restaurant::class.java)
                        restaurant?.id = documentSnapshot.id
                        restaurant
                    }
            } catch (e: Exception) {
                throw e
            }
        }


    suspend fun getProducts(restaurantId: String): List<Product> = withContext(Dispatchers.IO) {
        try {
            FirebaseFirestore.getInstance().collection("restaurants").document(restaurantId)
                .collection("products").get().await()
                .documents.mapNotNull { documentSnapshot ->
                    val product = documentSnapshot.toObject(Product::class.java)
                    product?.id = documentSnapshot.id
                    product
                }
        } catch (e: Exception) {
            throw e
        }
    }

    suspend fun getRatings(restaurantId: String): List<Rating> = withContext(Dispatchers.IO) {
        try {
            FirebaseFirestore.getInstance().collection("restaurants").document(restaurantId)
                .collection("ratings").get().await()
                .documents.mapNotNull { it.toObject(Rating::class.java) }
        } catch (e: Exception) {
            throw e
        }
    }


    suspend fun getRestaurant(restaurantId: String): Restaurant =
        withContext(Dispatchers.IO) {
            try {
                var restaurant =
                    FirebaseFirestore.getInstance().collection("restaurants").document(restaurantId)
                        .get().await().toObject(Restaurant::class.java)!!
                restaurant.id = restaurantId
                restaurant
            } catch (e: Exception) {
                throw e
            }
        }


    fun setProduct(product: Product, restaurantId: String, productId: String) {
        FirebaseFirestore.getInstance().collection("restaurants").document(restaurantId)
            .collection("products")
            .document(productId).update(
                "name", product.name, "price", product.price,
                "description", product.description, "section", product.section
            )

    }

    suspend fun uploadPhoto(uri: Uri): Uri = withContext(Dispatchers.IO) {
        try {
            Firebase.storage.reference.child("images/${uri.lastPathSegment}").putFile(uri)
                .await().storage.downloadUrl.await()
        } catch (e: Exception) {
            throw e
        }
    }

    fun deleteProduct(restaurantId: String, productId: String) {
        FirebaseFirestore.getInstance().collection("restaurants").document(restaurantId)
            .collection("products")
            .document(productId).delete()
    }

    fun deleteRestaurant(restaurantId: String) {
        FirebaseFirestore.getInstance().collection("restaurants").document(restaurantId).delete()
    }
}
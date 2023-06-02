package com.example.eatit.data

import androidx.annotation.WorkerThread
import com.example.eatit.EatItApp
import com.example.eatit.model.Restaurant
import com.google.firebase.firestore.FirebaseFirestore


class RestaurantsRepository(eatItApp: EatItApp) {

    @WorkerThread
    suspend fun insertNewRestaurant(restaurant: Restaurant) {
        FirebaseFirestore.getInstance().collection("restaurants").add(restaurant)
    }
}
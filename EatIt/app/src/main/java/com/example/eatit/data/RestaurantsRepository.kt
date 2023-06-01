package com.example.eatit.data

import androidx.annotation.WorkerThread
import kotlinx.coroutines.flow.Flow

class RestaurantsRepository(private val restaurantsDAO:RestaurantsDAO) {

    val restaurants: Flow<List<Restaurant>> = restaurantsDAO.getRestaurants()

    //@WorkerThread Denotes that the annotated method should only be called on a worker thread.
    //By default Room runs suspend queries off the main thread
    @WorkerThread
    suspend fun insertNewRestaurant(restaurant: Restaurant) {
        restaurantsDAO.insert(restaurant)
    }
}
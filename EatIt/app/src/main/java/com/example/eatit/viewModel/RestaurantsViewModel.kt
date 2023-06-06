package com.example.eatit.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.eatit.data.RestaurantsRepository
import com.example.eatit.model.Product
import com.example.eatit.model.Rating
import com.example.eatit.model.Restaurant
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RestaurantsViewModel @Inject constructor(
    private val repository: RestaurantsRepository
) : ViewModel() {
    private var _restaurantSelected: Restaurant? = null

    fun addNewRestaurant(restaurant: Restaurant) = viewModelScope.launch {
        repository.insertNewRestaurant(restaurant)
    }

    fun addNewProduct(product: Product) = viewModelScope.launch {
         repository.insertNewProduct(_restaurantSelected?.id, product)
    }

    suspend fun getRestaurants(): List<Restaurant> {
        return repository.getRestaurants()
    }
    suspend fun getRestaurantsByUserId(userId: String): List<Restaurant> {
        return repository.getRestaurantsByUserId(userId)
    }

    suspend fun getProducts(restaurantId: String): List<Product> {
        return repository.getProducts(restaurantId)
    }

    suspend fun getRatings(restaurantId: String): List<Rating> {
        return repository.getRatings(restaurantId)
    }

    suspend fun getRestaurant(restaurantId: String): Restaurant {
        return repository.getRestaurant(restaurantId)
    }

    val restaurantSelected
        get() = _restaurantSelected

    fun selectRestaurant(restaurant: Restaurant) {
        _restaurantSelected = restaurant
    }


}
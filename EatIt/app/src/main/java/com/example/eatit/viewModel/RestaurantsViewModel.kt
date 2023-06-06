package com.example.eatit.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.eatit.data.RestaurantsRepository
import com.example.eatit.model.Product
import com.example.eatit.model.Restaurant
import com.google.firebase.firestore.DocumentSnapshot
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RestaurantsViewModel @Inject constructor(
    private val repository: RestaurantsRepository
) : ViewModel() {
    private var _restaurantSelected: DocumentSnapshot? = null

    fun addNewRestaurant(restaurant: Restaurant) = viewModelScope.launch {
        repository.insertNewRestaurant(restaurant)
    }

    fun addNewProduct(product: Product) = viewModelScope.launch {
        repository.insertNewProduct(_restaurantSelected?.id.toString(), product)
    }

    suspend fun getRestaurants(): List<DocumentSnapshot> {
        return repository.getRestaurants()
    }

    fun getProducts(restaurantId: String): List<DocumentSnapshot> {
        return repository.getProducts(restaurantId)
    }

    fun getRatings(restaurantId: String): List<DocumentSnapshot> {
        return repository.getRatings(restaurantId)
    }

    suspend fun getRestaurant(restaurantId: String): MutableList<DocumentSnapshot> {
        return repository.getRestaurant(restaurantId)
    }

    val restaurantSelected
        get() = _restaurantSelected

    fun selectRestaurant(restaurant: DocumentSnapshot) {
        _restaurantSelected = restaurant
    }


}
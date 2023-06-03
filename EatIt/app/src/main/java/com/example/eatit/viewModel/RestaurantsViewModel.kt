package com.example.eatit.viewModel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.eatit.data.RestaurantsRepository
import com.example.eatit.model.Product
import com.example.eatit.model.Restaurant
import com.example.eatit.utilities.Filters
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RestaurantsViewModel @Inject constructor(
    private val repository: RestaurantsRepository
) : ViewModel() {
    var filters: Filters = Filters.default
    private var _restaurantSelected: Restaurant? = null
    private var _restaurantFromGPS = mutableStateOf("")
    fun addNewRestaurant(restaurant: Restaurant) = viewModelScope.launch {
        repository.insertNewRestaurant(restaurant)
        resetGPSRestaurant()
    }

    fun addNewProduct(product: Product) = viewModelScope.launch {
        repository.insertNewProduct(_restaurantSelected?.id.toString(), product)
    }

    val restaurantSelected
        get() = _restaurantSelected

    fun selectRestaurant(restaurant: Restaurant) {
        _restaurantSelected = restaurant
    }

    val restaurantFromGPS
        get() = _restaurantFromGPS

    fun setGPSRestaurant(restaurant: String) {
        _restaurantFromGPS.value = restaurant
    }

    private fun resetGPSRestaurant() {
        _restaurantFromGPS.value = ""
    }


}
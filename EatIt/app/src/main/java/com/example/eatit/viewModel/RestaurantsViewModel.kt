package com.example.eatit.viewModel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.eatit.data.RestaurantsRepository
import com.example.eatit.model.Product
import com.example.eatit.model.Restaurant
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RestaurantsViewModel @Inject constructor(
    private val repository: RestaurantsRepository
) : ViewModel() {
    fun addNewRestaurant(restaurant: Restaurant) = viewModelScope.launch {
        repository.insertNewRestaurant(restaurant)
    }

    fun addNewProduct(product: Product) = viewModelScope.launch {
        repository.insertNewProduct(_restaurantSelected?.id.toString(), product)
    }

    private var _restaurantSelected: Restaurant? = null
    val restaurantSelected
        get() = _restaurantSelected

    fun selectRestaurant(restaurant: Restaurant) {
        _restaurantSelected = restaurant
    }


    private var _restaurantFromGPS = mutableStateOf("")
    val restaurantFromGPS
        get() = _restaurantFromGPS

    fun setGPSRestaurant(restaurant: String) {
        _restaurantFromGPS.value = restaurant
    }

    private fun resetGPSRestaurant() {
        _restaurantFromGPS.value = ""
    }


}
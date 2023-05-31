package com.example.eatit.viewModel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.eatit.data.Restaurant
import com.example.eatit.data.RestaurantsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RestaurantsViewModel @Inject constructor(
    private val repository: RestaurantsRepository
) : ViewModel() {

    val restaurants = repository.restaurants

    fun addNewRestaurant(restaurant: Restaurant) = viewModelScope.launch {
        repository.insertNewRestaurant(restaurant)
        resetGPSRestaurant()
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
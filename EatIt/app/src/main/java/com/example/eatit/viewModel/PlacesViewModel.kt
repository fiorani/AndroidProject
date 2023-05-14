package com.example.eatit.viewModel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.eatit.data.Place
import com.example.eatit.data.PlacesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PlacesViewModel @Inject constructor(
    private val repository: PlacesRepository
) : ViewModel() {

    val places = repository.places

    fun addNewPlace(place: Place) = viewModelScope.launch {
        repository.insertNewPlace(place)
        resetGPSPlace()
    }

    private var _placeSelected: Place? = null
    val placeSelected
        get() = _placeSelected

    fun selectPlace(place: Place) {
        _placeSelected = place
    }


    private var _placeFromGPS = mutableStateOf("")
    val placeFromGPS
        get() = _placeFromGPS

    fun setGPSPlace(place: String) {
        _placeFromGPS.value = place
    }

    private fun resetGPSPlace() {
        _placeFromGPS.value = ""
    }
}
package com.example.eatit.viewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.eatit.data.CartRepository
import com.example.eatit.model.Order
import com.example.eatit.model.Product
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CartViewModel @Inject constructor(
    private val repository: CartRepository
) : ViewModel() {
    private var _orderLines: Order? = null
    fun addNewOrder(order: Order) = viewModelScope.launch {
        repository.insertNewOrder(order)
    }

    val orderSelected
        get() = _orderLines

    fun selectOrder(order: Order) {
        _orderLines = order
    }

    suspend fun getOrders(): List<Order> {
        return repository.getOrders()
    }

    suspend fun getProducts(order: Order): List<Product> {
        return repository.getProducts(order)
    }


}
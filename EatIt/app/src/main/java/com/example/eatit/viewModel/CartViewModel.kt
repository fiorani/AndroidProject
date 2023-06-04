package com.example.eatit.viewModel

import androidx.compose.runtime.Composable
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.eatit.data.CartRepository
import com.example.eatit.model.Order
import com.google.firebase.firestore.DocumentSnapshot
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

    val oderSelected
        get() = _orderLines

    fun selectOrder(order: Order) {
        _orderLines = order
    }

    fun reduceCount(product: DocumentSnapshot) = _orderLines?.reduceCount(product, _orderLines!!)

    fun increaseCount(product: DocumentSnapshot) =
        _orderLines?.increaseCount(product, _orderLines!!)

    @Composable
    fun getOrders(): List<DocumentSnapshot> {
        return repository.getOrders()
    }

    @Composable
    fun getProducts(order: DocumentSnapshot): SnapshotStateList<DocumentSnapshot> {
        return repository.getProducts(order)
    }


}
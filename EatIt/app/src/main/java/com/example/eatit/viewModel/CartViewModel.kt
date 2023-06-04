package com.example.eatit.viewModel

import androidx.lifecycle.ViewModel
import com.example.eatit.data.CartRepository
import com.example.eatit.model.Orders
import com.google.firebase.firestore.DocumentSnapshot
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class CartViewModel  @Inject constructor(
    private val repository: CartRepository
) : ViewModel() {
    private var _orderLines: Orders? = null

    val oderSelected
        get() = _orderLines

    fun selectOrder(order: Orders) {
        _orderLines = order
    }
    fun reduceCount(product: DocumentSnapshot)= _orderLines?.reduceCount(product, _orderLines!!)

    fun increaseCount(product: DocumentSnapshot)= _orderLines?.increaseCount(product, _orderLines!!)



}
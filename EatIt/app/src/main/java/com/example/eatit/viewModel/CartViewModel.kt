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

    fun reduceCount(product: DocumentSnapshot){
        val orderLines = _orderLines ?: return
        val productIndex = orderLines.listProductId?.indexOf(product.id)?: return
        val currentQuantity = orderLines.listQuantity?.get(productIndex)?.toIntOrNull() ?: return

        val newQuantity = currentQuantity - 1

        if (newQuantity == 0) {
            orderLines.listProductId?.removeAt(productIndex)
            orderLines.listQuantity?.removeAt(productIndex)
            selectOrder(orderLines)
            return
        }

        if (newQuantity > 0) {
            orderLines.listQuantity?.set(productIndex, newQuantity.toString())  // Update the quantity in the list
            // Save the updated orderLines to the repository or perform any other necessary actions
        }
        updatePrice()
        selectOrder(orderLines)
    }

    fun increaseCount(product: DocumentSnapshot) {
        val orderLines = _orderLines ?: return
        val productIndex = orderLines.listProductId?.indexOf(product.id)

        if (productIndex != null && productIndex != -1) {
            // Il prodotto esiste già nell'ordine, incrementa la quantità
            val currentQuantity = orderLines.listQuantity?.get(productIndex)?.toIntOrNull() ?: return
            val newQuantity = currentQuantity + 1

            orderLines.listQuantity?.set(productIndex, newQuantity.toString())  // Aggiorna la quantità nella lista
        } else {
            // Il prodotto non esiste nell'ordine, aggiungilo
            orderLines.listProductId?.add(product.id)
            orderLines.listPrice?.add(product.data!!["price"].toString())
            orderLines.listQuantity?.add("1")  // Imposta la quantità a 1 per il nuovo prodotto
        }

        // Salva le righe dell'ordine aggiornate nel repository o esegui altre azioni necessarie
        updatePrice()
        selectOrder(orderLines)
    }

    private fun updatePrice(){
        val orderLines = _orderLines ?: return
        var totalPrice = 0.0
        for (i in orderLines.listQuantity!!.indices){
            totalPrice += orderLines.listQuantity!![i].toDouble() * orderLines.listPrice!![i].toDouble()
        }
        orderLines.totalPrice = totalPrice
        selectOrder(orderLines)
    }

}
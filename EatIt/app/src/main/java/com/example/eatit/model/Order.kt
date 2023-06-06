package com.example.eatit.model

import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.ServerTimestamp
import com.google.firebase.ktx.Firebase
import java.util.Date

data class Order(
    var userId: String? = null,
    var restaurantId: String? = null,
    var listProductId: ArrayList<String>? = null,
    var listQuantity: ArrayList<String>? = null,
    var listPrice: ArrayList<String>? = null,
    var totalPrice: Double = 0.toDouble(),
    var id: String? = null,
    @ServerTimestamp var timestamp: Date? = null,
) {
    constructor(
        productId: ArrayList<String>,
        quantity: ArrayList<String>,
        prices: ArrayList<String>,
        price: Double,
        restaurantId: String?
    ) : this() {
        this.userId = Firebase.auth.uid
        this.restaurantId = restaurantId
        this.listProductId = productId
        this.listQuantity = quantity
        this.totalPrice = price
        this.listPrice = prices
    }

    fun reduceCount(product: Product, order: Order): Order {
        val orderLines = order
        val productIndex = orderLines.listProductId?.indexOf(product.id.toString())
        if (productIndex != null && productIndex != -1) {
            val currentQuantity =
                orderLines.listQuantity?.get(productIndex)?.toIntOrNull() ?: return orderLines
            val newQuantity = currentQuantity.minus(1)
            if (newQuantity == 0) {
                orderLines.listProductId?.removeAt(productIndex)
                orderLines.listQuantity?.removeAt(productIndex)
                orderLines.listPrice?.removeAt(productIndex)
                return updatePrice(orderLines)
            }
            if (newQuantity > 0) {
                orderLines.listQuantity?.set(
                    productIndex,
                    newQuantity.toString()
                )  // Update the quantity in the list
                // Save the updated orderLines to the repository or perform any other necessary actions
            }
        }

        return updatePrice(orderLines)
    }

    fun increaseCount(product: Product, order: Order): Order {
        val orderLines = order
        val productIndex = orderLines.listProductId?.indexOf(product.id.toString())

        if (productIndex != null && productIndex != -1) {
            // Il prodotto esiste già nell'ordine, incrementa la quantità
            val currentQuantity =
                orderLines.listQuantity?.get(productIndex)?.toIntOrNull() ?: return orderLines
            val newQuantity = currentQuantity.plus(1)
            orderLines.listQuantity?.set(
                productIndex,
                newQuantity.toString()
            )  // Aggiorna la quantità nella lista
        } else {
            // Il prodotto non esiste nell'ordine, aggiungilo
            orderLines.listProductId?.add(product.id.toString())
            orderLines.listPrice?.add(product.price.toString())
            orderLines.listQuantity?.add("1")  // Imposta la quantità a 1 per il nuovo prodotto
        }

        // Salva le righe dell'ordine aggiornate nel repository o esegui altre azioni necessarie
        return updatePrice(orderLines)
    }

    private fun updatePrice(order: Order): Order {
        val orderLines = order
        var totalPrice = 0.0
        for (i in orderLines.listQuantity!!.indices) {
            totalPrice += orderLines.listQuantity!![i].toDouble() * orderLines.listPrice!![i].toDouble()
        }
        orderLines.totalPrice = totalPrice
        return orderLines
    }
}

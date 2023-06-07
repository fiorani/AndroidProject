package com.example.eatit.model

import com.google.firebase.auth.ktx.auth
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
        var productIndex = 0
        if(order.listProductId?.contains(product.id.toString()) == true) {
            productIndex = order.listProductId?.indexOf(product.id.toString())!!
            order.listQuantity?.set(
                productIndex,
                order.listQuantity?.get(productIndex)?.toInt()?.minus(1).toString()
            )
        } else {
            order.listProductId?.remove(product.id.toString())
            order.listPrice?.removeAt(productIndex)
            order.listQuantity?.removeAt(productIndex)
        }

        return updatePrice(order)
    }

    fun increaseCount(product: Product, order: Order): Order {
        if(order.listProductId?.contains(product.id.toString()) == true) {
            val productIndex = order.listProductId?.indexOf(product.id.toString())!!
            order.listQuantity?.set(
                productIndex,
                order.listQuantity?.get(productIndex)?.toInt()?.plus(1).toString()
            )
        } else {
            order.listProductId?.add(product.id.toString())
            order.listPrice?.add(product.price.toString())
            order.listQuantity?.add("1")
        }
        return updatePrice(order)
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

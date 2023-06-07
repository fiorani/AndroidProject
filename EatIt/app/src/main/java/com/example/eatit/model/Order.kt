package com.example.eatit.model

import com.google.firebase.firestore.ServerTimestamp
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
    fun reduceCount(product: Product): Order {
        var productIndex = 0
        if (this.listProductId?.contains(product.id.toString()) == true) {
            productIndex = this.listProductId?.indexOf(product.id.toString())!!
            this.listQuantity?.set(
                productIndex,
                this.listQuantity?.get(productIndex)?.toInt()?.minus(1).toString()
            )
        } else {
            this.listProductId?.remove(product.id.toString())
            this.listPrice?.removeAt(productIndex)
            this.listQuantity?.removeAt(productIndex)
        }

        return updatePrice()
    }

    fun increaseCount(product: Product): Order {
        if (this.listProductId?.contains(product.id.toString()) == true) {
            val productIndex = this.listProductId?.indexOf(product.id.toString())!!
            this.listQuantity?.set(
                productIndex,
                this.listQuantity?.get(productIndex)?.toInt()?.plus(1).toString()
            )
        } else {
            this.listProductId?.add(product.id.toString())
            this.listPrice?.add(product.price.toString())
            this.listQuantity?.add("1")
        }
        return updatePrice()
    }

    private fun updatePrice(): Order {
        var totalPrice = 0.0
        for (i in this.listQuantity!!.indices) {
            totalPrice += this.listQuantity!![i].toDouble() * this.listPrice!![i].toDouble()
        }
        this.totalPrice = totalPrice
        return this
    }
}

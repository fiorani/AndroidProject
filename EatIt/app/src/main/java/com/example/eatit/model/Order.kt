package com.example.eatit.model

import com.google.firebase.firestore.ServerTimestamp
import java.util.Date

data class Order(
    var userId: String = "",
    var restaurantId: String = "",
    var listProductId: ArrayList<String> = ArrayList(mutableListOf()),
    var listQuantity: ArrayList<Int> = ArrayList(mutableListOf()),
    var listPrice: ArrayList<Float> = ArrayList(mutableListOf()),
    var totalPrice: Float = 0.0f,
    var id: String? = "",
    @ServerTimestamp var timestamp: Date = Date(),
) {
    fun reduceCount(product: Product): Order {
        var productIndex = 0
        if (this.listProductId.contains(product.id)) {
            productIndex = this.listProductId.indexOf(product.id)
            this.listQuantity.set(
                productIndex,
                this.listQuantity.get(productIndex).minus(1)
            )
        } else {
            this.listProductId.remove(product.id)
            this.listPrice.removeAt(productIndex)
            this.listQuantity.removeAt(productIndex)
        }

        return updatePrice()
    }

    fun increaseCount(product: Product): Order {
        if (this.listProductId.contains(product.id)) {
            val productIndex = this.listProductId.indexOf(product.id)
            this.listQuantity.set(
                productIndex,
                this.listQuantity.get(productIndex).plus(1)
            )
        } else {
            this.listProductId.add(product.id!!)
            this.listPrice.add(product.price)
            this.listQuantity.add(1)
        }
        return updatePrice()
    }

    private fun updatePrice(): Order {
        var totalPrice = 0.0f
        for (i in this.listQuantity.indices) {
            totalPrice += this.listQuantity[i].toFloat() * this.listPrice[i]
        }
        this.totalPrice = totalPrice
        return this
    }
}

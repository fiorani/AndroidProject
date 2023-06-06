package com.example.eatit.util

import com.example.eatit.model.Restaurant

/**
 * Utilities for Restaurants.
 */
object RestaurantUtil {


    /**
     * Get price represented as dollar signs.
     */
    fun getPriceString(restaurant: Restaurant): String {
        return getPriceString(restaurant.price)
    }

    /**
     * Get price represented as dollar signs.
     */
    fun getPriceString(priceInt: Int): String {
        return when (priceInt) {
            1 -> "$"
            2 -> "$$"
            3 -> "$$$"
            else -> "$$$"
        }
    }
}

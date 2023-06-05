package com.example.eatit.util

import android.content.Context
import com.example.eatit.R
import com.example.eatit.model.Restaurant
import java.util.Locale
import java.util.Random

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
